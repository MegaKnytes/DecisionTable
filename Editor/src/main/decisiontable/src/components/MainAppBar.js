import {AppBar, Box, Button, IconButton, Toolbar, Typography} from "@mui/material";

export default function MainAppBar() {
    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Decision Table Editor
                    </Typography>
                </Toolbar>
            </AppBar>
        </Box>
    );
}