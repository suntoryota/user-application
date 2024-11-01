package com.example.demo.ui.views;

import com.example.demo.constant.AppConstant;
import com.example.demo.constant.BaseResponse;
import com.example.demo.domain.dto.response.UserResponse;
import com.example.demo.service.ReportService;
import com.example.demo.service.UserService;
import com.example.demo.ui.views.components.UserForm;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.io.ByteArrayInputStream;



import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route(value = "users", layout = MainView.class)
@PageTitle("User Management")
@Slf4j
public class UserListView extends VerticalLayout {
    private final Grid<UserResponse> grid;
    private final UserForm form;
    private final UserService userService;
    private final TextField filterField;
    private final ReportService reportService;

    public UserListView(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
        this.grid = new Grid<>(UserResponse.class);
        this.form = new UserForm();
        this.filterField = new TextField();

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();
        configureFilter();

        Div content = new Div(getToolbar(), getContent());
        content.addClassName("content");
        content.setSizeFull();

        add(content);

        refreshGrid();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        grid.removeAllColumns();

        // Konfigurasi kolom
        grid.addColumn(UserResponse::getId)
                .setHeader("ID")
                .setWidth("100px")
                .setFlexGrow(0);

        grid.addColumn(UserResponse::getFirstName)
                .setHeader("First Name")
                .setFlexGrow(1);

        grid.addColumn(UserResponse::getLastName)
                .setHeader("Last Name")
                .setFlexGrow(1);

        grid.addColumn(UserResponse::getEmail)
                .setHeader("Email")
                .setFlexGrow(1);

        grid.addColumn(UserResponse::getPhoneNumber)
                .setHeader("Phone")
                .setFlexGrow(1);

        // Status column dengan badge
        grid.addComponentColumn(user -> {
            String theme = switch (user.getStatus()) {
                case ACTIVE -> "success";
                case BLOCKED -> "error";
                case INACTIVE -> "contrast";
            };

            Span badge = new Span(user.getStatus().toString());
            badge.getElement().getThemeList().add("badge");
            badge.getElement().getThemeList().add(theme);
            return badge;
        }).setHeader("Status").setFlexGrow(0);

        // Date formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        grid.addColumn(user -> user.getCreatedAt().format(formatter))
                .setHeader("Created At")
                .setFlexGrow(1);

        grid.addColumn(user ->
                        user.getUpdatedAt() != null ? user.getUpdatedAt().format(formatter) : "-")
                .setHeader("Updated At")
                .setFlexGrow(1);

        // Action column
        grid.addComponentColumn(user -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_TERTIARY_INLINE);
            editButton.addClickListener(e -> editUser(user));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
            deleteButton.addClickListener(e -> deleteUser(user));

            // Wrap buttons in a horizontal layout
            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Actions").setFlexGrow(0);

        // Make columns resizable
        grid.getColumns().forEach(col -> col.setResizable(true));

        // Add sorting
        grid.getColumns().forEach(col -> col.setSortable(true));

        // Add selection listener
        grid.asSingleSelect().addValueChangeListener(event -> {
            editUser(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveUser);
        form.addDeleteListener(this::deleteUser);
        form.addCloseListener(e -> closeEditor());
    }

    private void configureFilter() {
        filterField.setPlaceholder("Filter by name or email...");
        filterField.setClearButtonVisible(true);
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterField.setWidth("300px");
        filterField.addValueChangeListener(e -> refreshGrid());
    }

    private HorizontalLayout getToolbar() {
        Button addUserButton = new Button("Add User");
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserButton.setIcon(new Icon(VaadinIcon.PLUS));
        addUserButton.addClickListener(click -> addUser());

        Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(click -> loadUsers());

        // Export buttons
        Button pdfButton = new Button("Export PDF");
        pdfButton.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        pdfButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        pdfButton.addClickListener(e -> exportPdf());

        Button excelButton = new Button("Export Excel");
        excelButton.setIcon(new Icon(VaadinIcon.TABLE));
        excelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        excelButton.addClickListener(e -> exportExcel());

        HorizontalLayout toolbar = new HorizontalLayout(
                filterField,
                addUserButton,
                new HorizontalLayout(pdfButton, excelButton)
        );

        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);

        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void loadUsers() {
        try {
            String searchText = filterField.getValue();
            Pageable pageable = PageRequest.of(0, 100);
            BaseResponse<Page<UserResponse>> response = userService.getAllUsers(searchText, pageable);

            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                grid.setItems(response.getData().getContent());
            } else {
                Notification.show(
                        "Failed to load users: " + response.getMessage(),
                        3000,
                        Notification.Position.MIDDLE
                );
            }
        } catch (Exception e) {
            log.error("Error loading users", e);
            Notification.show(
                    "Error loading users: " + e.getMessage(),
                    3000,
                    Notification.Position.MIDDLE
            );
        }
    }

    private void saveUser(UserForm.SaveEvent event) {
        try {
            BaseResponse<UserResponse> response;
            if (event.getUserId() != null) {
                response = userService.updateUser(event.getUserId(), event.getUserRequest());
            } else {
                response = userService.createUser(event.getUserRequest());
            }

            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                Notification.show("User saved successfully",
                        3000, Notification.Position.MIDDLE);
                closeEditor();
                refreshGrid();
            } else {
                Notification.show("Error: " + response.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            log.error("Error saving user", e);
            Notification.show("Error saving user: " + e.getMessage(),
                    3000, Notification.Position.MIDDLE);
        }
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        try {
            BaseResponse<Void> response = userService.deleteUser(event.getUserId());
            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                Notification.show("User deleted successfully",
                        3000, Notification.Position.MIDDLE);
                closeEditor();
                refreshGrid();
            } else {
                Notification.show("Error: " + response.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            log.error("Error deleting user", e);
            Notification.show("Error deleting user: " + e.getMessage(),
                    3000, Notification.Position.MIDDLE);
        }
    }

    private void deleteUser(UserResponse user) {
        if (user == null) return;

        try {
            BaseResponse<Void> response = userService.deleteUser(user.getId());
            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                Notification.show("User deleted successfully",
                        3000, Notification.Position.MIDDLE);
                closeEditor();
                loadUsers();
            } else {
                Notification.show("Error: " + response.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            log.error("Error deleting user", e);
            Notification.show("Error deleting user: " + e.getMessage(),
                    3000, Notification.Position.MIDDLE);
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addUser() {
        grid.asSingleSelect().clear();
        editUser(new UserResponse());
    }

    private void editUser(UserResponse user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void exportPdf() {
        try {
            BaseResponse<byte[]> response = reportService.generateUserReport();
            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                String fileName = "users_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

                StreamResource resource = new StreamResource(fileName,
                        () -> new ByteArrayInputStream(response.getData()));

                Anchor link = new Anchor(resource, "");
                link.getElement().setAttribute("download", true);
                link.add(new Button("Download PDF"));

                Dialog dialog = new Dialog();
                dialog.add(new H3("Download Report"));
                dialog.add(new Paragraph("Your report is ready to download."));
                dialog.add(link);

                Button closeButton = new Button("Close", e -> dialog.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                dialog.add(new HorizontalLayout(closeButton));

                dialog.open();
            } else {
                Notification.show("Error generating PDF: " + response.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            log.error("Error exporting PDF", e);
            Notification.show("Error generating PDF report",
                    3000, Notification.Position.MIDDLE);
        }
    }


    private void exportExcel() {
        try {
            BaseResponse<byte[]> response = reportService.generateUserExcelReport();
            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                String fileName = "users_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

                StreamResource resource = new StreamResource(fileName,
                        () -> new ByteArrayInputStream(response.getData()));

                // Buat anchor untuk download
                Anchor link = new Anchor(resource, "");
                link.getElement().setAttribute("download", true);
                link.add(new Button("Download Excel"));

                // Buat dialog
                Dialog dialog = new Dialog();
                dialog.add(new H3("Download Report"));
                dialog.add(new Paragraph("Your report is ready to download."));
                dialog.add(link);

                Button closeButton = new Button("Close", e -> dialog.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                dialog.add(new HorizontalLayout(closeButton));

                dialog.open();
            } else {
                Notification.show("Error generating Excel: " + response.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            log.error("Error exporting Excel", e);
            Notification.show("Error generating Excel report",
                    3000, Notification.Position.MIDDLE);
        }
    }

    private void refreshGrid() {
        try {
            log.info("Loading users with search text: {}", filterField.getValue());
            Pageable pageable = PageRequest.of(0, 100);
            BaseResponse<Page<UserResponse>> response = userService.getAllUsers(filterField.getValue(), pageable);

            if (response.getStatus().equals(AppConstant.SUCCESS)) {
                java.util.List<UserResponse> users = response.getData().getContent();
                log.info("Found {} users", users.size());
                grid.setItems(users);
            } else {
                Notification.show(
                        "Failed to load users: " + response.getMessage(),
                        3000,
                        Notification.Position.MIDDLE
                );
            }
        } catch (Exception e) {
            log.error("Error loading users", e);
            Notification.show(
                    "Error loading users: " + e.getMessage(),
                    3000,
                    Notification.Position.MIDDLE
            );
        }
    }
}