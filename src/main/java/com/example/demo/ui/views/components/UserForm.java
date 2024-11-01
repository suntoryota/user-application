package com.example.demo.ui.views.components;

import com.example.demo.constant.AppConstant;
import com.example.demo.constant.UserStatus;
import com.example.demo.domain.dto.request.UserRequest;
import com.example.demo.domain.dto.response.UserResponse;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;


public class UserForm extends FormLayout {
    private final BeanValidationBinder<UserRequest> binder;
    private UserRequest userRequest;
    private Long userId;

    // Form fields
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final EmailField email = new EmailField("Email");
    private final TextField phoneNumber = new TextField("Phone Number");
    private final ComboBox<UserStatus> status = new ComboBox<>("Status");

    // Buttons
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    public UserForm() {
        addClassName("user-form");
        binder = new BeanValidationBinder<>(UserRequest.class);
        userRequest = new UserRequest();

        // Configure UI components
        configureFields();
        configureButtons();
        configureBinder();

        // Add components to form
        add(
                createTopSection(),
                createMainSection(),
                createButtonSection()
        );
    }

    private void configureBinder() {
        // First Name validation
        binder.forField(firstName)
                .withValidator(name -> name != null && !name.trim().isEmpty(),
                        "First name is required")
                .withValidator(name -> name.matches("^[a-zA-Z\\s]*$"),
                        "Only letters are allowed")
                .bind(UserRequest::getFirstName, UserRequest::setFirstName);

        // Last Name validation
        binder.forField(lastName)
                .withValidator(name -> name != null && !name.trim().isEmpty(),
                        "Last name is required")
                .withValidator(name -> name.matches("^[a-zA-Z\\s]*$"),
                        "Only letters are allowed")
                .bind(UserRequest::getLastName, UserRequest::setLastName);

        // Email validation
        binder.forField(email)
                .withValidator(new EmailValidator("Invalid email address"))
                .bind(UserRequest::getEmail, UserRequest::setEmail);

        // Phone validation
        binder.forField(phoneNumber)
                .withValidator(phone -> phone == null || phone.isEmpty() ||
                                phone.matches("^\\+?[0-9]{10,15}$"),
                        "minimal 10 character, number only")
                .bind(UserRequest::getPhoneNumber, UserRequest::setPhoneNumber);

        // Status validation
        binder.forField(status)
                .asRequired("Status is required")
                .bind(UserRequest::getStatus, UserRequest::setStatus);

        // Enable/disable save button based on form validity
        binder.addStatusChangeListener(event -> {
            boolean isValid = binder.isValid();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
        });
    }

    private void configureFields() {
        // First Name
        firstName.setRequired(true);
        firstName.setErrorMessage("First name is required");
        firstName.setPrefixComponent(VaadinIcon.USER.create());
        firstName.setValueChangeMode(ValueChangeMode.EAGER);

        // Last Name
        lastName.setRequired(true);
        lastName.setErrorMessage("Last name is required");
        lastName.setPrefixComponent(VaadinIcon.USER.create());
        lastName.setValueChangeMode(ValueChangeMode.EAGER);

        // Email
        email.setRequired(true);
        email.setErrorMessage("Valid email is required");
        email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        email.setHelperText("Format : xxx@email.domain");
        email.setValueChangeMode(ValueChangeMode.EAGER);

        // Phone
        phoneNumber.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneNumber.setHelperText("(Optional)");
        phoneNumber.setValueChangeMode(ValueChangeMode.EAGER);

        // Status
        status.setRequired(true);
        status.setItems(UserStatus.values());
        status.setValue(UserStatus.ACTIVE);
    }


    private void configureButtons() {
        // Save button
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());
        save.setEnabled(false);

        // Delete button
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.getStyle().set("margin-left", "auto");
        delete.setVisible(false);
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, userId)));

        // Cancel button
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
    }

    private Component createTopSection() {
        H3 title = new H3(userId == null ? "New User" : "Edit User");
        title.addClassName("form-title");

        return title;
    }

    private Component createMainSection() {
        return new VerticalLayout(
                firstName,
                lastName,
                email,
                phoneNumber,
                status
        );
    }

    private void configureValidation() {
        // First Name validation
        binder.forField(firstName)
                .withValidator(
                        name -> name != null && !name.trim().isEmpty(),
                        AppConstant.ValidationMessage.NAME_REQUIRED
                )
                .withValidator(
                        name -> name.matches("^[a-zA-Z\\s]{2,50}$"),
                        AppConstant.ValidationMessage.NAME_FORMAT
                )
                .withValidator(
                        name -> name.length() >= 2 && name.length() <= 50,
                        AppConstant.ValidationMessage.NAME_LENGTH
                )
                .bind(UserRequest::getFirstName, UserRequest::setFirstName);

        // Last Name validation
        binder.forField(lastName)
                .withValidator(
                        name -> name != null && !name.trim().isEmpty(),
                        AppConstant.ValidationMessage.NAME_REQUIRED
                )
                .withValidator(
                        name -> name.matches("^[a-zA-Z\\s]{2,50}$"),
                        AppConstant.ValidationMessage.NAME_FORMAT
                )
                .withValidator(
                        name -> name.length() >= 2 && name.length() <= 50,
                        AppConstant.ValidationMessage.NAME_LENGTH
                )
                .bind(UserRequest::getFirstName, UserRequest::setLastName);

        // Email validation
        binder.forField(email)
                .withValidator(
                        email -> email != null && !email.trim().isEmpty(),
                        AppConstant.ValidationMessage.EMAIL_REQUIRED
                )
                .withValidator(
                        new EmailValidator(AppConstant.ValidationMessage.INVALID_EMAIL)
                )
                .withValidator(
                        email -> email.length() <= 100,
                        AppConstant.ValidationMessage.EMAIL_LENGTH
                )
                .bind(UserRequest::getEmail, UserRequest::setEmail);

        // Phone validation
        binder.forField(phoneNumber)
                .withValidator(
                        phone -> phone == null || phone.isEmpty() ||
                                phone.matches("^\\+?[0-9]{10,15}$"),
                        AppConstant.ValidationMessage.PHONE_INVALID
                )
                .bind(UserRequest::getPhoneNumber, UserRequest::setPhoneNumber);

        // Status validation
        binder.forField(status)
                .asRequired(AppConstant.ValidationMessage.STATUS_REQUIRED)
                .bind(UserRequest::getStatus, UserRequest::setStatus);
    }

    private Component createButtonSection() {
        HorizontalLayout buttons = new HorizontalLayout(save, delete, cancel);
        buttons.addClassName("button-layout");
        return buttons;
    }

    public void setUser(UserResponse user) {
        this.userId = user != null ? user.getId() : null;

        userRequest = new UserRequest();
        if (user != null) {
            userRequest.setFirstName(user.getFirstName());
            userRequest.setLastName(user.getLastName());
            userRequest.setEmail(user.getEmail());
            userRequest.setPhoneNumber(user.getPhoneNumber());
            userRequest.setStatus(user.getStatus());
            delete.setVisible(true);
        } else {
            userRequest.setStatus(UserStatus.ACTIVE);
            delete.setVisible(false);
        }

        // Reset form validation
        binder.readBean(userRequest);

        // Clear validation errors
        firstName.setInvalid(false);
        lastName.setInvalid(false);
        email.setInvalid(false);
        phoneNumber.setInvalid(false);
    }

    public void clearForm() {
        userRequest = new UserRequest();
        userRequest.setStatus(UserStatus.ACTIVE);
        binder.readBean(userRequest);

        // Clear validation errors
        firstName.setInvalid(false);
        lastName.setInvalid(false);
        email.setInvalid(false);
        phoneNumber.setInvalid(false);

        // Reset buttons
        save.setEnabled(false);
        delete.setVisible(false);
    }

    private void validateAndSave() {
        try {
            // Validate form
            if (binder.validate().isOk()) {
                binder.writeBean(userRequest);
                fireEvent(new SaveEvent(this, userId, userRequest));
            }
        } catch (ValidationException e) {
            Notification.show("Please check the form for errors",
                    3000, Notification.Position.MIDDLE);
        }
    }

    // Event handling
    @Getter
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final Long userId;

        protected UserFormEvent(UserForm source, Long userId) {
            super(source, false);
            this.userId = userId;
        }
    }

    @Getter
    public static class SaveEvent extends UserFormEvent {
        private final UserRequest userRequest;

        SaveEvent(UserForm source, Long userId, UserRequest userRequest) {
            super(source, userId);
            this.userRequest = userRequest;
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, Long userId) {
            super(source, userId);
        }
    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}