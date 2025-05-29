import * as React from 'react';
import Container from '@mui/material/Container';
import MainAppBar from "@/components/MainAppBar";


export default function MainView() {
  return (
    <Container maxWidth={false} disableGutters>
      <MainAppBar/>
    </Container>
  );
}
