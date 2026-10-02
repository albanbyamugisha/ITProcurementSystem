# Simple password-reset form — class project

Planning only. Updated on 2 October 2026 to follow the user's request for a simpler local demonstration. No email, sender setup or verification codes are required.

## Flow

1. Click Forgot Password? on LoginFrame.
2. Enter the username and full name registered on the same account.
3. Click Reset Password.
4. If the details match, generate a new replacement password and update its hash in the database.
5. Show the new password only after saving succeeds. The user copies it and returns to login.

The original password cannot be recovered from the hash already stored. Displaying a newly generated password achieves the requested visible result while retaining hashed account storage. The replacement must satisfy the existing password-length policy. Use a short, commented helper with SecureRandom to generate it; no new external library is needed.

If the details are blank or do not match, show a simple error and change nothing. Clear the displayed password before another attempt and when closing the form. Never include it in PDFs, logs or Git. Preserve the account's role and invalidate old sessions after reset. A database failure must not display a password as successfully saved.

Username and full name are not secrets. This flow is a deliberately limited classroom demonstration: anyone who knows both could reset the account. It is not suitable as real-world identity verification.

## Controls

Use the JFrame Form `ForgotPasswordFrame` in package `itprocurementsystem`. It is a JFrame because it opens before login and therefore needs its own window. About 500 by 320 pixels is sufficient.

| Palette control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Reset Password |
| Label | jLabelUsername | Username |
| Text Field | jTextFieldUsername | Empty |
| Label | jLabelFullName | Full name |
| Text Field | jTextFieldFullName | Empty |
| Button | jButtonResetPassword | Reset Password |
| Label | jLabelNewPassword | Your new password |
| Text Field | jTextFieldNewPassword | Empty; editable = false; visible text that can be copied |
| Label | jLabelStatus | Enter your registered username and full name. |
| Button | jButtonBackToLogin | Back to Login |

The result uses a normal Text Field because the user explicitly wants the new password displayed. Read-only means it cannot be edited accidentally; it can still be selected and copied.

If the earlier email/code form was already drawn, reuse the same frame and replace those controls with this list. Add a Button named `jButtonForgotPassword`, labelled Forgot Password?, to LoginFrame. Do not write event code during the design step.

Registration still follows the separate agreed rule: successful account creation opens MainFrame automatically. Password reset returns to normal login.
