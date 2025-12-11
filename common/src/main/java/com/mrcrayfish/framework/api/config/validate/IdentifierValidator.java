package com.mrcrayfish.framework.api.config.validate;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class IdentifierValidator implements Validator<String>
{
    private static final IdentifierValidator INSTANCE = new IdentifierValidator(null);

    private final @Nullable String namespace;
    private final Component hint;

    private IdentifierValidator(@Nullable String namespace)
    {
        this.namespace = namespace;
        this.hint = namespace != null
                ? Component.translatable("framework.validator.resource_location.hint_with_namespace", namespace)
                : Component.translatable("framework.validator.resource_location.hint");
    }

    @Override
    public boolean test(String value)
    {
        Identifier result = Identifier.tryParse(value);
        return result != null && (this.namespace == null || this.namespace.equals(result.getNamespace()));
    }

    @Override
    public Component getHint()
    {
        return this.hint;
    }

    @Override
    public String getCommentHint()
    {
        if(this.namespace != null)
        {
            return "Valid value: A valid resource location but must use the namespace \"%1$s\". Format: \"%1$s:<path>\"".formatted(this.namespace);
        }
        return "Valid value: Any valid resource location. Format: \"<namespace>:<path>\"";
    }

    /**
     * @return A String validator that validates the String value uses the Identifier format.
     */
    public static IdentifierValidator any()
    {
        return INSTANCE;
    }

    /**
     * A String validator that validates the String value uses the Identifier format, however
     * the namespace is restricted to the given namespace. So if the given namespace is <code>backpacked</code>,
     * then only Identifiers, like <code>backpacked:shelf</code>, will be accepted.
     *
     * @param namespace the namespace to use for matching
     * @return A String validator for check Identifier format with a namespace rule.
     */
    public static IdentifierValidator restrictNamespace(String namespace)
    {
        Preconditions.checkState(Identifier.isValidNamespace(namespace), "Invalid namespace: %s", namespace);
        return new IdentifierValidator(namespace);
    }
}
