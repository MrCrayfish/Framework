package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.configuration.Acknowledge;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean optional = false;
    private final List<BiConsumer<Supplier<RegistryAccess>, SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> configurationMessages = new ArrayList<>();
    private final List<Function<SimpleChannel, ConfigurationTask>> configurationTasks = new ArrayList<>();

    public ForgeNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public ForgeNetworkBuilder optional()
    {
        this.optional = true;
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, codec, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        this.playMessages.add((access, channel) -> channel
            .play(protocol -> {
                protocol.flow(flow, registry -> {
                    registry.add(messageClass, codec, (msg, ctx) -> {
                        PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                        MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                        handler.accept(msg, context);
                        context.getReply().ifPresent(reply -> channel.reply(reply, ctx));
                    });
                });
            })
        );
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages)
    {
        return this.registerConfigurationMessage(name, taskClass, codec, handler, messages, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow)
    {
        this.registerConfigurationAckMessage();
        this.configurationMessages.add(channel -> channel
            .configuration(protocol -> {
                protocol.flow(flow, registry -> {
                    registry.add(taskClass, codec, (msg, ctx) -> {
                        PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                        MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                        FrameworkResponse response = handler.apply(msg, context::execute);
                        if(response.isError()) {
                            context.disconnect(Component.literal("Connection closed - " + response.message()));
                            return;
                        }
                        context.setHandled(true);
                        channel.reply(new Acknowledge(), ctx);
                    });
                });
            })
        );
        ResourceLocation taskId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        ConfigurationTask.Type type = new ConfigurationTask.Type(taskId.toString());
        this.configurationTasks.add(channel -> new ForgeConfigurationTask<>(channel, type, messages));
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            this.configurationMessages.add(channel -> channel
                .configuration()
                .serverbound()
                .add(Acknowledge.class, Acknowledge.STREAM_CODEC, (msg, ctx) -> {
                    PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                    MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                    Acknowledge.handle(msg, context);
                })
            );
        }
    }

    public FrameworkNetwork build()
    {
        return new ForgeNetwork(this.id, this.version, this.optional, this.playMessages, this.configurationMessages, this.configurationTasks);
    }
}
