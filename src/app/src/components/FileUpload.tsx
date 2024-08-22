import React from 'react';
import {Button, Grid, Paper} from '@mui/material';
import {AnonymizedResponse} from '../interfaces/AnonymizedResponse';
import {DropzoneArea} from "mui-file-dropzone";
import apiUrl from "../constant/apiUrl";
import {RootState} from "../redux/store";
import {useDispatch, useSelector} from "react-redux";
import {setAnonymizedData, setError, setLoading} from "../redux/slices/filesSlice";

const FileUpload: React.FC = () => {
    const dispatch = useDispatch();
    const [file, setFile] = React.useState<File | null>(null);
    const [fileObjects, setFileObjects] = React.useState<File[]>([]);
    const selectedRules = useSelector((state: RootState) => state.rules.selectedRules);


    const handleFileUpload = async () => {
        if (!file || selectedRules.length === 0) return;
        const dictFile = new Blob([JSON.stringify(selectedRules)], {type: 'text/plain'});
        const formData = new FormData();
        formData.append('file', file);
        formData.append('dictionary', dictFile);
        dispatch(setError(null));
        dispatch(setAnonymizedData(null));
        dispatch(setLoading(true));
        await new Promise(r => setTimeout(r, 1000));
        try {
            const response = await fetch(`${apiUrl}anonymize/`, {
                method: 'POST',
                body: formData,
            });
            if (!response.ok) throw new Error('Error anonymizing file');
            const data: AnonymizedResponse = await response.json();
            dispatch(setAnonymizedData(data));
        } catch (err) {
            dispatch(setError("Failed to anonymize file"));
        }
        dispatch(setLoading(false));
    };

    const handleDropZoneChange = (files: File[]) => {
        if (files.length === 0) return;
        const selectedFile = files[0];
        setFile(selectedFile);
        setFileObjects(files);
    };

    const handleFileDelete = () => {
        setFile(null);
        setFileObjects([]);
    };

    return (

        <Grid item xs={8} component={Paper} marginTop={3} padding={2} marginX={"auto"}>
            <DropzoneArea
                onChange={handleDropZoneChange}
                filesLimit={1}
                maxFileSize={100000000}
                fileObjects={fileObjects}
                onDelete={handleFileDelete}
                dropzoneText={"Drag and drop here or click to select a file for anonymization"}
                showFileNames={true}
                alertSnackbarProps={{
                    autoHideDuration: 3000,
                    anchorOrigin: {"vertical": "bottom", "horizontal": "right"}
                }}
            />

            {/* Centered Button */}
            <Grid container justifyContent="center" marginTop={2}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleFileUpload}
                    size="large"
                    disabled={!file || selectedRules.length === 0}
                >
                    Anonymize File
                </Button>
            </Grid>
        </Grid>
    );
};

export default FileUpload;
