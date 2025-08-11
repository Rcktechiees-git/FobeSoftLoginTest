# FobeSoft Login Test Scenarios Implementation

## Excel to Test Method Mapping

Based on the Excel file "FobeSoft Testing_System Integration Test.xlsx", all test scenarios have been implemented as @Test methods in FobeSoftLoginTest.java:

### Test Step 1.0: Check if Username & Password fields are editable
- **Expected Output**: Yes, The user can enter the value.
- **Implementation**: `usernamePasswordFieldsEditable()` - Tests that both fields accept input and verify values

### Test Step 2.0: Verify Remember me checkbox is selectable
- **Expected Output**: Users can click the Remember me check Box, it can working functionality.
- **Implementation**: `rememberMeCheckbox()` - Tests checkbox selection functionality

### Test Step 3.0: Verify Forgot password displays popup message
- **Expected Output**: Users can see the popup message.
- **Implementation**: `forgotPasswordDisplaysPopup()` - Tests forgot password link shows popup or redirects

### Test Step 4.0: Verify sign up page redirect
- **Expected Output**: Users can see the sign up page.
- **Implementation**: `signUpPageRedirect()` - Tests navigation to signup page

### Test Step 5.0: Verify password view icon functionality
- **Expected Output**: It should displayed the password in the Password field.
- **Implementation**: `passwordViewIconFunctionality()` - Tests password visibility toggle (if available)

### Test Step 6.0: Check validation when both fields are empty
- **Expected Output**: Users can see both validation messages - username is required and password is required.
- **Implementation**: `bothFieldsEmptyValidation()` - Tests validation messages for empty fields

### Test Step 7.0: Check validation when username is filled but password is empty
- **Expected Output**: Users can see validation "Password is required".
- **Implementation**: `usernameFilledPasswordEmptyValidation()` - Tests password required validation

### Test Step 8.0: Check validation when password is filled but username is empty
- **Expected Output**: Users can see validation "Username is required".
- **Implementation**: `passwordFilledUsernameEmptyValidation()` - Tests username required validation

### Test Step 9.0: Verify with wrong username and password
- **Expected Output**: Users can view the pop-up with an error message.
- **Implementation**: `invalidCredentialsShowError()` - Tests error message for invalid credentials

### Test Step 10.0: Verify with valid username and password
- **Expected Output**: User can see the page redirecting from Login page to Daily sales page.
- **Implementation**: `validCredentialsLoginSuccess()` - Tests successful login redirect (requires valid credentials)

## Existing Test Methods Retained

- `loginElementsPresent()` - Verifies all login page elements are displayed

## Notes

1. All test methods include proper wait conditions and error handling
2. Tests are designed to be independent and clean up after themselves
3. The `validCredentialsLoginSuccess()` test requires actual valid credentials to pass completely
4. The `passwordViewIconFunctionality()` test will skip if no password view icon is found
5. All validation tests use case-insensitive text matching for robustness