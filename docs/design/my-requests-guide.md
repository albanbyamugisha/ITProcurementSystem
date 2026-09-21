# My Requests design guide

This panel is currently empty. Its database query is ready, but controls and event wiring are still pending.
The reference image was generated using the built-in image-generation tool.

In MyRequestsPanel Design view, add:
- Label jLabelTitle, text My Requests, top left.
- Button jButtonRefresh, text Refresh, top right.
- Table jTableRequests in its scroll pane, below the heading.
- Label jLabelCount, text Requests: 0, below the table.

Set the table model to five columns: Request ID, Date Created, Status, Total, Notes.
Use zero rows and disable editing for all five columns. The blank rows in the image only illustrate spacing.
Allow the table to occupy most of the panel, approximately 760 by 500 pixels.

In MainFrame Design view, add a sidebar button below Requests:
- Variable name jButtonMyRequests.
- Text My Requests.

Save All. Do not write event code yet. We will connect the controls in Java, explain the code in comments, and restrict the new button to Requester users.

## Generation prompt

Create a crisp front-facing UI mockup for a beginner Java Swing JPanel named MyRequestsPanel. Simple standard Swing controls easily built in NetBeans Design view, light gray panel, navy text, no icons or custom components, no window chrome. Wide panel about 760 by 500 proportions. Top left large label 'My Requests', top right standard button 'Refresh'. Below a wide white table with five column headings exactly 'Request ID', 'Date Created', 'Status', 'Total', 'Notes'. Show blank table body with horizontal row guides, no sample data. Below table at left a small label 'Requests: 0'. Generous spacing, readable text. Outside the panel above it, caption 'MyRequestsPanel - design reference'. No other controls.

