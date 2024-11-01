package com.example.demo.ui.views;


import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@PageTitle("User Management System")
public class MainView extends AppLayout {
    private final Div pageContent;
    private boolean isFirstLoad = true;

    public MainView() {
        // Main content
        pageContent = new Div();
        pageContent.setSizeFull();

        // Add initial welcome message
        showWelcomeMessage();

        createHeader();
        createDrawer();

        setContent(pageContent);
    }

    private void showWelcomeMessage() {
        if (isFirstLoad) {
            VerticalLayout welcomeLayout = new VerticalLayout();
            welcomeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            welcomeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            welcomeLayout.setSizeFull();

            H2 welcomeMessage = new H2("Welcome to User Management System");
            Span hintMessage = new Span("Click 'Users List' to view data");

            welcomeMessage.getStyle()
                    .set("color", "var(--lumo-primary-text-color)")
                    .set("margin-bottom", "0");

            hintMessage.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-style", "italic");

            welcomeLayout.add(welcomeMessage, hintMessage);
            pageContent.removeAll();
            pageContent.add(welcomeLayout);
        }
    }

    private void createHeader() {
        H2 logo = new H2("User Management System");
        logo.getStyle()
                .set("font-size", "1.2em")
                .set("margin", "0")
                .set("font-weight", "600");

        DrawerToggle toggle = new DrawerToggle();

        HorizontalLayout header = new HorizontalLayout(toggle, logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        header.getStyle().set("min-height", "50px");

        addToNavbar(header);
    }

    private void createDrawer() {
        // Create navigation title
        H3 navTitle = new H3("Navigation");
        navTitle.getStyle()
                .set("font-size", "1em")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("padding", "var(--lumo-space-m)")
                .set("margin", "0");

        // Create navigation link
        RouterLink listUsers = new RouterLink("Users List", UserListView.class);
        listUsers.setHighlightCondition(HighlightConditions.sameLocation());
        listUsers.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("color", "var(--lumo-body-text-color)")
                .set("text-decoration", "none")
                .set("transition", "background-color 0.3s");

        // Add icon to nav item
        Icon userIcon = VaadinIcon.USERS.create();
        userIcon.getStyle().set("margin-right", "var(--lumo-space-s)");
        listUsers.getElement().insertChild(0, userIcon.getElement());

        // Create navigation container
        VerticalLayout navLayout = new VerticalLayout(navTitle, listUsers);
        navLayout.setPadding(false);
        navLayout.setSpacing(false);

        addToDrawer(navLayout);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            isFirstLoad = false;
            pageContent.removeAll();
            pageContent.getElement().appendChild(content.getElement());
        }
    }
}