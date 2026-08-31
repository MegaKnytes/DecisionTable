This folder contains the DTP Editor GUI's React source code.

# Rule.js

Rule.js contains all the classes to store the XML tags that are being edited.
Most classes represent a single tag, with attributes and children as properties in the class.
Any exceptions are noted in the comments on that class. 
The UI stores its data in these classes.
There are functions that are intended to partially check the input data to make sure it is valid.
Each class will eventually have a toXML() function that will convert it to an XML string for output.
Currently only DecisionTable, Rule, Condition and Action tags have toXML().
This is sufficient to save a table but not a config

Currently, the Device class doesn't have anything that handles the many possible input and output parameters devices can have.
See the Device View section for more comments on this.

# Editor.js

Editor.js has a central class with a single static instance (Editor.instance) that keeps track of the DTP currently being edited.
Its methods allow editing getting of Rules, Conditions, and VariableGroups by index, as well as allowing setting the name and members.
The UI components make calls to this instance to keep track of the rules between views.

# UI

page.js contains the overall layout, including the drawer and menu bar.
The main view in the center is changed by the drawer buttons Variables, Devices, Rules, and State Machines.
The current plan for this drawer is that it will look more like an IDE's file explorer, 
with each of those categories acting like a folder and the individual devices, groups, rules, etc. acting like files in those folders.

Currently, clicking on these views opens a single instance (i.e. Rules always opens the first rule).
Further, Devices has not been implemented and simply opens the VariableGroup view.

These views and their components are in the components folder. 
It might make sense to further group the components by group (e.g. Variables/VariableViewComponent.js) as the project grows.

The menu bar buttons (other than the drawer one) are not currently functional, and might warrant removal.
The drawer save/load buttons also are not functional, but they are probably a better starting point.

## Variable View

The main component here is VariableViewComponent. 
This has the name box and the VariableGroup table.
It represents a single variable group, and users can add members to it and set their initial value.
The save button is currently part of VariableGroup instead of VariableViewComponent, 
which was due to a misunderstanding about how passing values between children works, 
and can be changed to match the RuleComponent.js save button if it is preferred by users.

## State View

The main component here is StateViewComponent.
This has the name box and the StateValueTable.
It represents a single state, and users can add values by name.
Like VariableGroup, the save button is currently part of StateValueTable instead of StateViewComponent,
and can be changed to match the RuleComponent.js save button if it is preferred by users.

## Rule View

The main component here is RuleComponent. 
RuleComponent holds the save button, the name, and the debug toggle (the last of which is currently not functional).
It then has two tables, one for conditions and one for actions.
The conditions table stores targets, comparisons, and values.
The comparison is a ButtonGroup in its own RuleComparison component.
The action table stores targets and values.
Changes to the text boxes in the tables are reported back to the RuleComponent, which keeps track of their current states to make the save button functional.
This can be implemented in State View and Variable View to move the save button up to the same line as the name box.

## Device View

Currently, this doesn't exist and redirects to Variable View.
The plan is that it will have a table that allows you to select the device name in DTP, hardwaremap name, and any other parameters.
Some things (e.g. the Mecanum Drive) require many parameters, while others (e.g. a Servo) require very few.
This would be dependent on the drivers, and as such it would be subject to change as drivers change.
Making this part easy for use with many drivers and easy to add drivers to is something that I plan to spend more time considering before implementing.

Rule.js also requires additional changes to properly store devices.
I think a driver class that has a list of all the input and output parameters from a given device would be the right way,
but having a way to show that in the ui requires some thought.

# Ideas and plans

* First and foremost the toXML function needs to be implemented, which will make saving functional.
  Running the validity checks on the table and config during this time will also be helpful.
* There needs to be a place to edit the overall DTP things, like name, enabled, type (TELEOP/AUTO/UTILITY), etc.
* The Device view needs to be implemented
* The drawer should have its contents rearranged, and icons made functional.
* Save, Load, and other "File" functions should be moved entirely to drawer or entirely to menu bar, 
  rather than having buttons in both places (should be done when functionality is added)
* Sending the XML string from React to the Java server and saving it needs to be investigated, although it shouldn't be too difficult
* Variable View, State View, and Rule View should have the location of their save buttons standardized
  * There might be a better way to communicate that that saves the changes to the Group/State/Rule rather than the table
  * Indicating when changes have been made might also be worthwhile.
* The Drawer needs to have the ability to select the specific rule/variablegroup/state
  * A dropdown accordion thing like Android Studio or VS Code's Files seems like the right choice
* Currently, the toXML() functions do not handle indentation. 
  Adding indentation would make the output XML much more readable.