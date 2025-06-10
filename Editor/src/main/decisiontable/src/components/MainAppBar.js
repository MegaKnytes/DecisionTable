import {AppBar, Box, Button, IconButton, Toolbar, Typography, ButtonGroup} from "@mui/material";
import MenuIcon from '@mui/icons-material/Menu';

export default function MainAppBar(handleDrawerOpen) {
    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static" color="primary">
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={[
                        {
                        mr: 2,
                        },
                        open && { display: 'none' },
                        ]}
                        >
                        <MenuIcon />
                    </IconButton>
                </Toolbar>
            </AppBar>
        </Box>
    );
}