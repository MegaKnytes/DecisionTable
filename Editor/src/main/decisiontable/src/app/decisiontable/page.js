'use client'

import * as React from 'react';

// Material-UI Components
import { styled, useTheme } from '@mui/material/styles';
import Container from '@mui/material/Container';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import CssBaseline from '@mui/material/CssBaseline';
import MuiAppBar from '@mui/material/AppBar';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import {Box, Button, IconButton, Toolbar, Typography, ButtonGroup} from "@mui/material";

// Material-UI Icons
import AddIcon from '@mui/icons-material/Add';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import DevicesIcon from '@mui/icons-material/Devices';
import FileOpenIcon from '@mui/icons-material/FileOpen';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import ListItemIcon from '@mui/material/ListItemIcon';
import MailIcon from '@mui/icons-material/Mail';
import MenuIcon from '@mui/icons-material/Menu';
import SaveAsIcon from '@mui/icons-material/SaveAs';
import SaveIcon from '@mui/icons-material/Save';

// Custom Components
import RuleComponent from "@/components/RuleComponent";
import StateViewComponent from "@/components/StateViewComponent";
import VariableViewComponent from "@/components/VariableViewComponent";

// Editor state class
import {Editor} from "@/Editor.js"

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
  const [open, setOpen] = React.useState(false); //Tracks whether the drawer is open or closed
  const [currentPage, setCurrentPage] = React.useState(<VariableViewComponent editor={Editor.instance} index="0" />); //Holds the type of the current page

  //Functions to handle clicking on the different views
  const handleVariableClick = () => {
    setCurrentPage(<VariableViewComponent editor={Editor.instance} index="0" />);
  }
  const handleDeviceClick  = () => {
    setCurrentPage(<VariableViewComponent editor={Editor.instance} index="0" />);
  }
  const handleRuleClick  = () => {
    setCurrentPage(<RuleComponent editor={Editor.instance} index="0" />);
  }
  const handleStatesClick  = () => {
    setCurrentPage(<StateViewComponent editor={Editor.instance} index="0" />);
  }

  //Functions to handle opening/closing drawer
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
          <ListItem key="Variables" disablePadding>
            <ListItemButton onClick={handleVariableClick}>
              <ListItemIcon>
                <InboxIcon />
              </ListItemIcon>
              <ListItemText primary="Variables" />
            </ListItemButton>
          </ListItem>
          <ListItem key="Devices" disablePadding>
            <ListItemButton onClick={handleDeviceClick}>
              <ListItemIcon>
                <DevicesIcon />
              </ListItemIcon>
              <ListItemText primary="Devices" />
            </ListItemButton>
          </ListItem>
          <ListItem key="Rules" disablePadding>
            <ListItemButton onClick={handleRuleClick}>
              <ListItemIcon>
                <MailIcon />
              </ListItemIcon>
              <ListItemText primary="Rules" />
            </ListItemButton>
          </ListItem>
          <ListItem key="State Machines" disablePadding>
            <ListItemButton onClick={handleStatesClick}>
              <ListItemIcon>
                <ChevronLeftIcon />
              </ListItemIcon>
              <ListItemText primary="State Machines" />
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>
    <Main open={open}>
        {currentPage}
    </Main>
    </Container>
  );
}
