# NotificationsPanel design
Create a JPanel Form named NotificationsPanel. It belongs inside MainFrame.
Design only; no events or database implementation now.

| Control | Variable name | Text/model |
| --- | --- | --- |
| Label | jLabelTitle | My Notifications |
| Label | jLabelFilter | Show |
| Combo Box | jComboBoxFilter | All notifications; Unread; Read |
| Button | jButtonRefresh | Refresh |
| Table | jTableNotifications | Notification ID, Date, Message, Status |
| Label | jLabelMessage | Selected message |
| Text Area | jTextAreaMessage | Select a notification to read its message. |
| Label | jLabelUnreadCount | Unread: 0 |
| Button | jButtonMarkRead | Mark Selected as Read |
| Button | jButtonMarkAllRead | Mark All as Read |

Table: four columns, zero rows, all not editable, inside a scroll pane.
Text area: editable false, lineWrap true, wrapStyleWord true, inside a scroll pane.
Combo-box choices are separate lines in the normal model editor.
MainFrame: add jButtonNotifications with text Notifications. Save All.
The future implementation must limit all reads and updates to the signed-in user's notifications.

## Generation prompt
Generated with the built-in image-generation tool.

Create a crisp front-facing beginner Java Swing JPanel design reference, outside caption 'NotificationsPanel - design reference'. Pale gray panel, navy text, standard rectangular controls, no icons or window chrome. Inside heading 'My Notifications'. Top row label 'Show', combo 'All notifications', button 'Refresh'. Below large empty table with exactly four headings 'Notification ID', 'Date', 'Message', 'Status'. Below label 'Selected message', wide read-only multiline text area showing 'Select a notification to read its message.' Bottom left label 'Unread: 0'. Bottom right buttons 'Mark Selected as Read' and 'Mark All as Read'. 850x600 proportions, clean legible spacing, no sample messages. JLabel JComboBox JButton JTable JTextArea JScrollPane only.

