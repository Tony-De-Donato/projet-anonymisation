import React from 'react';
import {Grid, Typography} from '@mui/material';

const Header: React.FC = () => (
    <Grid item xs={12} borderBottom={1} padding={2} bgcolor={'#2d3c5e'} color={'white'}
          textAlign={'center'}>
        <Typography variant="h4">Anonymization App</Typography>
    </Grid>
);

export default Header;
