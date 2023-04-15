package se.montesmites.ekonomi.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.IconFactory;

public record NavigationTarget(
    String label, Class<? extends Component> target, IconFactory iconFactory) {}
