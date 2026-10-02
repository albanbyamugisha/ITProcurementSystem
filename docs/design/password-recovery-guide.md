# Forgotten-password form plan

Planning only. No form or reset code has been implemented yet.

## Proposed user flow

1. LoginFrame has a Forgot Password button.
2. ForgotPasswordFrame asks for the registered email address and sends a reset code.
3. The user enters the received code, a new password and its confirmation.
4. After successful validation, replace the saved password hash and consume the code in one transaction.
5. Return to login after a reset. Automatic login is reserved for successful new-account registration, as requested.

Codes should be unpredictable, stored as hashes, expire after a short period (proposed: 10 minutes), be single-use, and have attempt/resend limits. Display the same neutral response for unknown and known email addresses. Send codes only to the stored account address, never to a replacement address typed during recovery. Do not display codes in the public form, logs or PDFs. Resetting a password must invalidate outstanding reset codes and existing sessions.

Email delivery needs a configured sender/provider. SMTP credentials must stay in local private configuration, not source code or Git. The user should never paste a mailbox password into chat. Real email delivery cannot be described as working until it has been configured and tested.

If email delivery is unavailable for the assignment demo, a separately agreed admin-assisted recovery flow could issue an expiring code after identity verification through a trusted channel. Knowing a username or email address is not sufficient verification. Do not silently fall back to showing reset codes to whoever requests them.

Reference: https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html

## NetBeans controls to prepare

Create a JFrame Form named `ForgotPasswordFrame` in `itprocurementsystem`. A JFrame is suitable because password recovery is available before the user can enter MainFrame. Keep it simple: about 520 by 430 pixels with aligned labels and fields.

| Palette control | Variable name | Text/purpose |
| --- | --- | --- |
| Label | jLabelTitle | Reset Password |
| Label | jLabelEmail | Registered email |
| Text Field | jTextFieldEmail | Empty |
| Button | jButtonSendCode | Send Reset Code |
| Label | jLabelCode | Reset code |
| Text Field | jTextFieldResetCode | Empty |
| Label | jLabelNewPassword | New password |
| Password Field | jPasswordFieldNewPassword | Empty |
| Label | jLabelConfirmPassword | Confirm new password |
| Password Field | jPasswordFieldConfirmPassword | Empty |
| Label | jLabelStatus | Enter your registered email to request a reset code. |
| Button | jButtonResetPassword | Reset Password |
| Button | jButtonBackToLogin | Back to Login |

Use Password Field controls for both passwords. Put the email and Send Reset Code controls first, followed by the reset code and password fields. The Reset Password and Back to Login buttons belong at the bottom. Do not add event code while preparing the design.

Add a Button named `jButtonForgotPassword`, labelled Forgot Password?, to LoginFrame. Registration automatic login reuses MainFrame and needs no additional form.

Later code should explain why this form is a JFrame, centre it and return safely to LoginFrame when it closes. The form alone must never be treated as identity verification.
