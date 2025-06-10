'use client'

import * as React from 'react';
import Container from '@mui/material/Container';
import MainAppBar from "@/components/MainAppBar";
import RuleComponent from "@/components/RuleComponent";

import RuleTable from "@/components/RuleTable";
import { styled, useTheme } from '@mui/material/styles';
import Drawer from '@mui/material/Drawer';
import CssBaseline from '@mui/material/CssBaseline';
import MuiAppBar from '@mui/material/AppBar';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';
import {Box, Button, IconButton, Toolbar, Typography, ButtonGroup} from "@mui/material";
import MenuIcon from '@mui/icons-material/Menu';
import DevicesIcon from '@mui/icons-material/Devices';
import SaveAsIcon from '@mui/icons-material/SaveAs';
import SaveIcon from '@mui/icons-material/Save';
import FileOpenIcon from '@mui/icons-material/FileOpen';
import AddIcon from '@mui/icons-material/Add';

const drawerWidth = 240;



const Main = styled('main', {
  shouldForwardProp: (prop) => prop !== 'open',
})(({ theme }) => ({
  transition: theme.transitions.create(['margin', 'width'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  variants: [
    {
      props: ({ open }) => open,
      style: {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: `${drawerWidth}px`,
        transition: theme.transitions.create(['margin', 'width'], {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
      },
    },
  ],
}));


const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== 'open',
})(({ theme }) => ({
  transition: theme.transitions.create(['margin', 'width'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  variants: [
    {
      props: ({ open }) => open,
      style: {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: `${drawerWidth}px`,
        transition: theme.transitions.create(['margin', 'width'], {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
      },
    },
  ],
}));

const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: 'flex-end',
}));


export default function MainView() {
  const theme = useTheme();
  const [open, setOpen] = React.useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };
  return (
        <Container maxWidth={false} disableGutters>
          <CssBaseline />
          <AppBar position="static" open={open}>
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
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Decision Table Editor
                    </Typography>
                    <ButtonGroup variant="contained" color="secondary">
                        <Button>File</Button>
                        <Button>Device</Button>
                        <Button>Rule</Button>
                    </ButtonGroup>
            </Toolbar>
          </AppBar>
          <Drawer
            sx={{
              width: drawerWidth,
              flexShrink: 0,
              '& .MuiDrawer-paper': {
                width: drawerWidth,
                boxSizing: 'border-box',
              },
            }}
            variant="persistent"
            anchor="left"
            open={open}
            color="primary"
          >
            <DrawerHeader>
              <IconButton onClick={handleDrawerClose}>
                {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
              </IconButton>
            </DrawerHeader>
            <Divider />
            <List>
              {['New DTP', 'Open DTP', 'Save', 'Save as', 'New Robot Config', 'Save Robot Config'].map((text, index) => (
                <ListItem key={text} disablePadding>
                  <ListItemButton>
                    <ListItemIcon>
                      {index === 0 ? (
                          <AddIcon />
                        ) : index === 1 ? (
                          <FileOpenIcon />
                        ) : index === 2 ? (
                        <SaveIcon />
                      ) : index === 3 ? (
                            <SaveAsIcon />
                          ) : index === 4 ? (
                        <AddIcon />
                      ): (
                          <SaveIcon />
                        )}
                    </ListItemIcon>
                    <ListItemText primary={text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
            <Divider />
            <List>
              {['Variables', 'Devices', 'Rules'].map((text, index) => (
                <ListItem key={text} disablePadding>
                  <ListItemButton>
                    <ListItemIcon>
                      {index === 0 ? (
                        <InboxIcon /> // For 'Variables' (index 0)
                      ) : index === 1 ? (
                        <DevicesIcon /> // For 'Devices' (index 1)
                      ) : (
                        <MailIcon /> // For 'Rules' (index 2, or any other index)
                      )}
                    </ListItemIcon>
                    <ListItemText primary={text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Drawer>
        <Main open={open}>
            <RuleComponent/>
        </Main>
    </Container>
  );
}
