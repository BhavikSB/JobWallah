
import './App.css';
import {createTheme, MantineProvider } from '@mantine/core';
import '@mantine/core/styles.css';
import '@mantine/carousel/styles.css';
import '@mantine/tiptap/styles.css';
import '@mantine/dates/styles.css';
import '@mantine/notifications/styles.css';
import { Notifications } from '@mantine/notifications';
import { Provider } from 'react-redux';
import Store from './Store';
import AppRoutes from './Pages/AppRoutes';
// @ts-ignore
import AOS from 'aos';
import 'aos/dist/aos.css';
import { useEffect } from 'react';
function App() {
  useEffect(()=>{
    AOS.init({
      offset: 0,
      duration:800,
      easing:'ease-out'
    });
    AOS.refresh();
  }, []);

  const theme = createTheme({
    focusRing: "never",
    fontFamily: 'Poppins, sans-serif',
    primaryColor: 'brightSun',
    primaryShade: 4,
    colors: {
      'brightSun': ['#ebf8ff', '#d1ecff', '#a7dbff', '#74c2ff', '#3aa6ff', '#007efc', '#0062d1', '#004ba6', '#00397d', '#002f63', '#001836'
      ],
      'mineShaft': ['#f6f6f6', '#e7e7e7', '#d1d1d1', '#b0b0b0', '#888888', '#6d6d6d', '#5d5d5d', '#4f4f4f', '#454545', '#3d3d3d', '#2d2d2d',]
    }
  })
  return (
    <Provider store={Store}>
    <MantineProvider defaultColorScheme="dark" theme={theme} >
       <Notifications  position="top-center" zIndex={2001} />
      <AppRoutes/>
    </MantineProvider>
    </Provider>
  );
}

export default App;
