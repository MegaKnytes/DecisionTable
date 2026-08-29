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

//Holds an entire Variable Group
export default function VariableViewComponent({editor, index}) {
    const [alignment, setAlignment] = React.useState('web');

    const handleChange = (event, newAlignment) => {
        setAlignment(newAlignment);
    };

    const variableGroup = editor.getVariableGroup(index);
    let name = variableGroup.name;

    return (
        <Box>
            <Box sx={{ flexGrow: 1, margin: 1 }}>
                <TextField id="device" label="Variable Group Name" variant="outlined" defaultValue={name} sx={{ flexGrow: 1, margin: 1 }}/>
            </Box>
            <Box sx={{ flexGrow: 1, margin: 1 }}>
                <VariableGroup variableGroup={variableGroup}/>
            </Box>
        </Box>
    );
}