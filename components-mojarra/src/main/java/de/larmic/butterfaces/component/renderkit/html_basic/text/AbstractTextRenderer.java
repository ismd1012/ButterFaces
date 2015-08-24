package de.larmic.butterfaces.component.renderkit.html_basic.text;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import de.larmic.butterfaces.component.html.HtmlInputComponent;
import de.larmic.butterfaces.component.html.HtmlTooltip;
import de.larmic.butterfaces.component.html.InputComponentFacet;
import de.larmic.butterfaces.component.partrenderer.*;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public abstract class AbstractTextRenderer<T extends HtmlInputComponent> extends de.larmic.butterfaces.component.renderkit.html_basic.HtmlBasicRenderer {

    private static final Attribute[] INPUT_ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);

    @Override
    public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
        this.encodeBegin(context, component, "");
    }

    public void encodeBegin(final FacesContext context, final UIComponent component, final String additionalStyleClass) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        super.encodeBegin(context, component);

        final T htmlComponent = (T) component;

        final ResponseWriter writer = context.getResponseWriter();

        // Open outer component wrapper div
        new OuterComponentWrapperPartRenderer().renderComponentBegin(component, writer, additionalStyleClass);

        // Render label if components label attribute is set
        new LabelPartRenderer().renderLabel(component, writer);

        this.encodeBeginInnerWrapper(component, writer);
        this.encodeReadonly(htmlComponent, writer);
    }

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        final T htmlComponent = (T) component;
        final ResponseWriter writer = context.getResponseWriter();

        if (!htmlComponent.isReadonly()) {
            this.encodeEndContent(context, component, htmlComponent, writer);
        }

        this.encodeInnerEnd(component, writer);

        this.encodeEndInnerWrapper(component, writer);

        renderTooltipIfNecessary(context, component);

        this.encodeEnd(component, writer);

        // Open outer component wrapper div
        new OuterComponentWrapperPartRenderer().renderComponentEnd(writer);
    }

    protected void renderTooltipIfNecessary(final FacesContext context, final UIComponent component) throws IOException {
        for (UIComponent uiComponent : component.getChildren()) {
            if (uiComponent instanceof HtmlTooltip) {
                uiComponent.encodeAll(context);
                if (uiComponent.isRendered()) {
                    break;
                }
            }
        }

        new TooltipPartRenderer().renderTooltipIfNecessary(context, component);
    }

    protected void encodeEndContent(FacesContext context, UIComponent component, HtmlInputComponent htmlComponent, ResponseWriter writer) throws IOException {
        final UIComponent inputGroupAddonLeftFacet = component.getFacet(InnerComponentWrapperPartRenderer.INPUT_GROUP_ADDON_LEFT);
        final UIComponent inputGroupAddonRightFacet = component.getFacet(InnerComponentWrapperPartRenderer.INPUT_GROUP_ADDON_RIGHT);
        final UIComponent inputGroupBtnLeftFacet = component.getFacet(InnerComponentWrapperPartRenderer.INPUT_GROUP_BTN_LEFT);
        final UIComponent inputGroupBtnRightFacet = component.getFacet(InnerComponentWrapperPartRenderer.INPUT_GROUP_BTN_RIGHT);
        if (htmlComponent.getSupportedFacets().contains(InputComponentFacet.BOOTSTRAP_INPUT_GROUP_LEFT_ADDON) && inputGroupAddonLeftFacet != null) {
            writer.startElement("span", component);
            writer.writeAttribute("class", "input-group-addon", null);
            inputGroupAddonLeftFacet.encodeAll(context);
            writer.endElement("span");
        }
        if (htmlComponent.getSupportedFacets().contains(InputComponentFacet.BOOTSTRAP_INPUT_GROUP_LEFT_BTN) && inputGroupBtnLeftFacet != null) {
            writer.startElement("span", component);
            writer.writeAttribute("class", "input-group-btn", null);
            inputGroupBtnLeftFacet.encodeAll(context);
            writer.endElement("span");
        }
        super.encodeEnd(context, component);
        this.postEncodeInput(context, component);
        if (htmlComponent.getSupportedFacets().contains(InputComponentFacet.BOOTSTRAP_INPUT_GROUP_LEFT_ADDON) && inputGroupAddonRightFacet != null) {
            writer.startElement("span", component);
            writer.writeAttribute("class", "input-group-addon", null);
            inputGroupAddonRightFacet.encodeAll(context);
            writer.endElement("span");
        }
        if (htmlComponent.getSupportedFacets().contains(InputComponentFacet.BOOTSTRAP_INPUT_GROUP_LEFT_BTN) && inputGroupBtnRightFacet != null) {
            writer.startElement("span", component);
            writer.writeAttribute("class", "input-group-btn", null);
            inputGroupBtnRightFacet.encodeAll(context);
            writer.endElement("span");
        }
    }

    protected void postEncodeInput(final FacesContext context, final UIComponent component) throws IOException {

    }

    /**
     * @return true if normal readonly render option should be used. False is custom readonly option is created by renderer implementation.
     */
    protected boolean encodeReadonly() {
        return true;
    }

    protected void encodeReadonly(T htmlComponent, ResponseWriter writer) throws IOException {
        if (encodeReadonly()) {
            // Render readonly span if components readonly attribute is set
            new ReadonlyPartRenderer().renderReadonly(htmlComponent, writer);
        }
    }

    protected void encodeEndInnerWrapper(UIComponent component, ResponseWriter writer) throws IOException {
        // Close inner component wrapper div
        new InnerComponentWrapperPartRenderer().renderInnerWrapperEnd(component, writer);
    }

    protected void encodeBeginInnerWrapper(UIComponent component, ResponseWriter writer) throws IOException {
        // Open inner component wrapper div
        new InnerComponentWrapperPartRenderer().renderInnerWrapperBegin(component, writer);
    }

    protected void encodeInnerEnd(final UIComponent component, final ResponseWriter writer) throws IOException {
        // override me
    }

    protected void encodeEnd(final UIComponent component, final ResponseWriter writer) throws IOException {
        // override me
    }

    /**
     * Helper to call super end from sub classes without calling encodeEnd of this class. This is not nice but is works at this time.
     * TODO fix it.
     */
    protected void encodeSuperEnd(final FacesContext context, final UIComponent component) throws IOException {
        super.encodeEnd(context, component);
    }

    /**
     * Method copied from super class to add html features.
     */
    @Override
    protected void getEndTextToRender(final FacesContext context,
                                      final UIComponent component,
                                      final String currentValue) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();

        if (component instanceof UIInput) {
            writer.startElement("input", component);

            writer.writeAttribute("name", (component.getClientId(context)), "clientId");

            if (currentValue != null) {
                writer.writeAttribute("value", currentValue, "value");
            }

            // default
            this.renderBooleanValue(component, writer, "disabled");
            this.renderBooleanValue(component, writer, "ismap");
            this.renderBooleanValue(component, writer, "readonly");

            // html 5
            this.renderStringValue(component, writer, "autocomplete", "off");
            this.renderStringValue(component, writer, "placeholder");
            this.renderBooleanValue(component, writer, "autofocus");
            this.renderBooleanValue(component, writer, "pattern");
            this.renderBooleanValue(component, writer, "min");
            this.renderBooleanValue(component, writer, "max");

            // html
            this.renderStringValue(component, writer, "alt");
            this.renderStringValue(component, writer, "dir");
            this.renderStringValue(component, writer, "lang");
            this.renderStringValue(component, writer, "maxlength");
            this.renderStringValue(component, writer, "role");
            this.renderStringValue(component, writer, "size");
            this.renderStringValue(component, writer, "style");
            this.renderStringValue(component, writer, "tabindex");
            this.renderStringValue(component, writer, "title");

            // events
            this.renderEventValue(component, writer, "onblur", "blur");
            this.renderEventValue(component, writer, "onclick", "click");
            this.renderEventValue(component, writer, "ondblclick", "dblclick");
            this.renderEventValue(component, writer, "onfocus", "focus");
            this.renderEventValue(component, writer, "onkeydown", "keydown");
            this.renderEventValue(component, writer, "onkeypress", "keypress");
            this.renderEventValue(component, writer, "onkeyup", "keyup");
            this.renderEventValue(component, writer, "onmousedown", "mousedown");
            this.renderEventValue(component, writer, "onmousemove", "mousemove");
            this.renderEventValue(component, writer, "onmouseout", "mouseout");
            this.renderEventValue(component, writer, "onmouseover", "mouseover");
            this.renderEventValue(component, writer, "onmouseup", "mouseup");
            this.renderEventValue(component, writer, "onselect", "select");
            this.renderEventValue(component, writer, "onchange", "change");

            this.renderInputStyleClass((HtmlInputComponent) component, writer);

            this.renderStringValue(component, writer, "type");

            this.renderAdditionalInputAttributes(context, component, writer);

            writer.endElement("input");
        }
    }

    /**
     * When using {@link AbstractTextRenderer} input component will be wrapped. Component style class will be set to
     * component wrapper. This method renders inner input component bootstrap classes and validation markers.
     */
    protected void renderInputStyleClass(final HtmlInputComponent component,
                                         final ResponseWriter writer) throws IOException {
        final String validationMarkerClass = !component.isValid() ? Constants.INVALID_STYLE_CLASS : null;
        final String styleClass = StringUtils.concatWithSpace(
                Constants.INPUT_COMPONENT_MARKER,
                Constants.BOOTSTRAP_FORM_CONTROL,
                validationMarkerClass);
        writer.writeAttribute("class", styleClass, "styleClass");
    }

    protected void renderAdditionalInputAttributes(final FacesContext context,
                                                   final UIComponent component,
                                                   final ResponseWriter writer) throws IOException {
    }

    @Override
    public void encodeChildren(final FacesContext context,
                               final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        if (component.getChildCount() > 0) {
            for (UIComponent child : component.getChildren()) {
                // ignore tooltips (will be rendered before)
                if (!(child instanceof HtmlTooltip)) {
                    encodeRecursive(context, child);
                }
            }
        }
    }
}
