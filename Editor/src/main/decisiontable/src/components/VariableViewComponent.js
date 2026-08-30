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
import VariableGroup from "@/components/VariableGroup";

import {Variable} from "@/Rule.js"


//Holds an entire Variable Group
export default function VariableViewComponent({editor, index}) {
  const [alignment, setAlignment] = React.useState('web');

  const handleChange = (event, newAlignment) => {
    setAlignment(newAlignment);
  };

  let variableGroup = editor.getVariableGroup(index);
  let name = variableGroup.name;

  const handleSave = (rows) => {
    let variables = [];

    for(let variable = 0; variable < rows.length; variable++) {
        let row = rows[variable];
        console.log(row);
        variables.push(new Variable(row.name,row.type,row.value,""));
    }


    editor.setVariableGroupVariables(index, variables);
    editor.setVariableGroupName(index, groupName);

    console.log(variableGroup);
  }

  const [groupName, setGroupName] = React.useState(variableGroup.name);

  const handleNameChange = (event) => {
    setGroupName(event.target.value);
  }

  return (
    <Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <TextField id="device" label="Variable Group Name" variant="outlined" defaultValue={name} sx={{ flexGrow: 1, margin: 1 }} onChange={(e)=>handleNameChange(e)}/>
      </Box>
      <Box sx={{ flexGrow: 1, margin: 1 }}>
        <VariableGroup variableGroup={editor.getVariableGroup(index)} editor={editor} varGroupIndex={index} handleSave={handleSave} />
      </Box>
    </Box>
  );
}