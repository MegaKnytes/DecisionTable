'use client'

import * as React from 'react'

// Material-UI components
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';

// Material-UI Icons
import AddIcon from '@mui/icons-material/Add';

//Custom Components
import StateValueTable from "@/components/StateValueTable";

//Classes for DTP
import {StateValue} from "@/Rule.js"


//Holds an entire Variable Group
export default function StateViewComponent({editor, index}) {
  //Gets data from editor and stores it to more easily accessed variables
  let state = editor.getState(index);
  let name = state.name;

  //Handles a click on the save button, which is in the StateMachine component.
  //This function is passes to the StateMachine component for that purpose.
  const handleSave = (rows) => {
    let values = [];

    for(let value = 0; value < rows.length; value++) {
        let row = rows[value];
        values.push(new StateValue(row.name));
    }

    //Update editor data
    editor.setStateValues(index, values);
    editor.setStateName(index, groupName);

    //Log to console for debugging
    console.log(state);
  }

  //Variables to keep track of the name in the textbox
  const [groupName, setGroupName] = React.useState(state.name);

  //Handles changes to the name textbox
  const handleNameChange = (event) => {
    setGroupName(event.target.value);
  }

  return (
    <Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <TextField id="device" label="State Machine Name" variant="outlined" defaultValue={name} sx={{ flexGrow: 1, margin: 1 }} onChange={(e)=>handleNameChange(e)}/>
      </Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <StateValueTable state={editor.getState(index)} editor={editor} stateIndex={index} handleSave={handleSave} />
      </Box>
    </Box>
  );
}