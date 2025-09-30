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
import RuleCondition from "@/components/RuleCondition";
import RuleTable from "@/components/RuleTable";

export default function RuleComponent() {
    const [alignment, setAlignment] = React.useState('web');

    const handleChange = (event, newAlignment) => {
        setAlignment(newAlignment);
    };

    return (
        <Box>
            <Box sx={{ flexGrow: 1, margin: 1 }}>
                <TextField id="device" label="Rule Name" variant="outlined" sx={{ flexGrow: 1, margin: 1 }}/>
                <FormControlLabel control={<Checkbox size="large" color="error"/>} label="Debug" />
            </Box>
            <Box sx={{ flexGrow: 1, margin: 1 }}>
                <RuleTable inputOrOutput="Input" />
                <RuleTable inputOrOutput="Output" />
            </Box>
        </Box>
    );
}