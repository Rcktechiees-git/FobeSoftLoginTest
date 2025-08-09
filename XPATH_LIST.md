# XPath Expressions in FobeSoftLoginTest

This document lists all XPath expressions found in the FobeSoftLoginTest repository, organized by their purpose and functionality.

## Overview
- **Total unique XPath expressions:** 7
- **Files containing XPaths:** 2
- **Test framework:** Selenium WebDriver with TestNG

## XPath Expressions by Category

### 1. Page Header Elements

#### Login Page Header
```xpath
//h2[contains(text(), 'Log In')]
```
- **Purpose:** Locates the main login page header
- **Usage:** Wait condition to ensure login page is loaded
- **Found in:** Both test files (multiple occurrences)
- **Test methods:** `loginElementsPresent()`, `forgotPasswordLink()`, `signUpLink()`

### 2. Form Input Elements

#### Username Input Field
```xpath
//input[@formcontrolname='username']
```
- **Purpose:** Locates the username input field
- **Usage:** Element interaction for entering username
- **Found in:** Both test files (multiple occurrences)
- **Test methods:** `loginElementsPresent()`, `invalidLoginShowsError()`

#### Password Input Field
```xpath
//input[@formcontrolname='password']
```
- **Purpose:** Locates the password input field
- **Usage:** Element interaction for entering password
- **Found in:** Both test files (multiple occurrences)
- **Test methods:** `loginElementsPresent()`, `invalidLoginShowsError()`

### 3. Button Elements

#### Login Button
```xpath
//button[contains(text(), 'Log In')]
```
- **Purpose:** Locates the login submit button
- **Usage:** Element interaction for form submission
- **Found in:** Both test files (multiple occurrences)
- **Test methods:** `loginElementsPresent()`, `invalidLoginShowsError()`

### 4. Checkbox Elements

#### Remember Me Checkbox
```xpath
//input[@type='checkbox']
```
- **Purpose:** Locates the "Remember Me" checkbox
- **Usage:** Element interaction for checkbox selection
- **Found in:** Both test files (multiple occurrences)
- **Test methods:** `loginElementsPresent()`, `rememberMeCheckbox()`

### 5. Error Message Elements

#### Error Message (Basic Version)
```xpath
//*[contains(text(),'invalid') or contains(text(),'Incorrect') or contains(@class,'error')]
```
- **Purpose:** Locates error messages after invalid login attempts
- **Usage:** Validation of error display after failed login
- **Found in:** Root level `FobeSoftLoginTest.java`
- **Test methods:** `invalidLoginShowsError()`

#### Error Message (Enhanced Version)
```xpath
//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect') or contains(@class,'error')]
```
- **Purpose:** Locates error messages with case-insensitive text matching
- **Usage:** Enhanced validation of error display after failed login
- **Found in:** `src/test/java/FobeSoftLoginTest.java`
- **Test methods:** `invalidLoginShowsError()`
- **Enhancement:** Uses `translate()` function for case-insensitive matching

## File Locations

### 1. Root Level Test File
**File:** `/FobeSoftLoginTest.java`
- Contains 12 XPath usages
- Basic XPath implementations

### 2. Maven Structure Test File  
**File:** `/src/test/java/FobeSoftLoginTest.java`
- Contains 13 XPath usages
- Enhanced XPath with case-insensitive error detection

## XPath Best Practices Observed

1. **Attribute-based selection:** Using `@formcontrolname`, `@type` attributes for reliable element identification
2. **Text content matching:** Using `contains(text(), ...)` for dynamic text matching
3. **Multiple condition OR logic:** Combining multiple conditions for robust error detection
4. **Case-insensitive matching:** Using `translate()` function for text normalization
5. **Flexible element selection:** Using `//*` for any element type when appropriate

## Usage Statistics

| XPath Expression | Occurrences | Files |
|------------------|------------|-------|
| `//h2[contains(text(), 'Log In')]` | 6 | Both |
| `//input[@formcontrolname='username']` | 4 | Both |
| `//input[@formcontrolname='password']` | 4 | Both |
| `//button[contains(text(), 'Log In')]` | 4 | Both |
| `//input[@type='checkbox']` | 4 | Both |
| Error XPath (basic) | 1 | Root only |
| Error XPath (enhanced) | 1 | src/test only |

## Notes

- The repository contains duplicate test files with slight variations
- The `src/test/java/` version appears to be the updated/improved version
- All XPaths are designed for the FobeSoft login page at `https://dev.fobesoft.com/#/login`
- XPaths are used with Selenium WebDriver for automated testing