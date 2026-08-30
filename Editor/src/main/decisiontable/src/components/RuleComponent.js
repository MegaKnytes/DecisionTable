'use client'

import * as React from 'react'

// Material-UI components
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';

// Material-UI Icons
import AddIcon from '@mui/icons-material/Add';

//Custom Components
import ConditionTable from "@/components/ConditionTable";
import ActionTable from "@/components/ActionTable";

//Classes for DTP
import {Condition, Action} from "@/Rule.js"


//Holds all of the display for a single rule
export default function RuleComponent({editor, index}) {
  //Gets data from editor and stores it to more easily accessed variables
  let rule = editor.getRule(index);
  let name = rule.name;

  //Variables to keep track of the name in the textbox
  const [ruleName, setRuleName] = React.useState(rule.name);
  const [conditions, setConditions] = React.useState([]);
  const [actions, setActions] = React.useState([]);

  //Handles updates to the actions on changes.
  //This function is passes to the VariableGroup component for that purpose.
  const handleLogConditions = (rows) => {
    let newConditions = [];

    for(let condition = 0; condition < rows.length; condition++) {
        let row = rows[condition];
        console.log(row);
        newConditions.push(new Condition(row.target,row.condition,row.value));
    }

    setConditions(newConditions);
  }

  const handleLogActions = (rows) => {
      let newActions = [];

      for(let action = 0; action < rows.length; action++) {
          let row = rows[action];
          console.log(row);
          newActions.push(new Action(row.target,row.value));
      }

      setActions(newActions);
    }

  const handleSave = () => {
    //Update editor data
    editor.setRuleConditions(index, conditions);
    editor.setRuleActions(index, actions);
    editor.setRuleName(index, ruleName);

    //Log to console for debugging
    console.log(rule);
  }

  //Handles changes to the name textbox
  const handleNameChange = (event) => {
    setRuleName(event.target.value);
  }

  return (
    <Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <TextField id="device" label="Rule Name" variant="outlined" defaultValue={name} sx={{ flexGrow: 1, margin: 1 }} onChange={(e)=>handleNameChange(e)}/>
        <FormControlLabel control={<Checkbox size="large" color="error"/>} label="Debug" />
        <Button variant="contained" onClick={() => handleSave()}>Save</Button>
      </Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <ConditionTable editor={editor} ruleIndex={index} rule={rule} handleConditions={handleLogConditions} />
        {<ActionTable editor={editor} ruleIndex={index} rule={rule} handleActions={handleLogActions} />}
      </Box>
    </Box>
  );
}