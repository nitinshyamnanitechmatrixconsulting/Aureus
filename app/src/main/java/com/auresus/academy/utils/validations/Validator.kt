package com.auresus.academy.utils.validations

import com.auresus.academy.utils.validations.ValidationHelper
import com.auresus.academy.utils.validations.ValidationResult


class Validator constructor(var validationHelper: ValidationHelper) {


    fun validateEmail(email: String): Boolean {
        return if (!validationHelper.isEmptyField(email))
            if (!validationHelper.isValid6Digit(email))
                validationHelper.isValidEmail(email)
            else false
        else false
    }


    fun validatePassword(password: String): ValidationResult {
        return if (!validationHelper.isEmptyField(password))
            if (validationHelper.isValidPassword(password))
                ValidationResult.SUCCESS
            else ValidationResult.ERROR_PASSWORD
        else ValidationResult.EMPTY_PASSWORD
    }

    fun validatePasswordSignup(password: String): ValidationResult {
        return if (!validationHelper.isEmptyField(password))
            if (validationHelper.isValidPasswordMinLength(password))
                if (validationHelper.isValidPasswordMaxLength(password))
                    ValidationResult.SUCCESS
                else ValidationResult.ERROR_PASSWORD_TOO_LONG
            else ValidationResult.ERROR_PASSWORD_TOO_SMALL
        else ValidationResult.EMPTY_PASSWORD
    }


}