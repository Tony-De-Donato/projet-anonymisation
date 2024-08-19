import React from 'react';
import {Alert, Grid, Paper,} from '@mui/material';
import CircularProgress from "@mui/material/CircularProgress";

interface FileLoadingAndErrorProps {
    isLoading: boolean;
    error: string | null;
}

const FileLoadingAndError: React.FC<FileLoadingAndErrorProps> = ({
                                                                     isLoading,
                                                                     error
                                                                 }) => {


    return (

        <Grid container
              style={{
                  zIndex: 100,
                  position: 'fixed',
                  top: 10,
                  right: 10,
              }}
              justifyContent="flex-end"
              alignItems="flex-start"
        >
            <Grid item>
                <Paper style={{
                    textAlign: 'center',
                    fontSize: "x-large",
                    backgroundColor: "rgb(237,237,237)"
                }}>
                    {isLoading && <p style={{padding: "10px 20px 10px 20px"}}><CircularProgress/></p>}
                    {error && <p><Alert severity="error">{error}</Alert></p>}
                </Paper>
            </Grid>
        </Grid>

    );
};

export default FileLoadingAndError;

