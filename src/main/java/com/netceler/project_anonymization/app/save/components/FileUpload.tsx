import React from 'react';
import {Button, Grid, Paper,} from '@mui/material';

import {AnonymizedResponse} from '../interfaces/AnonymizedResponse';
import {RegexRule} from '../interfaces/RegexRule';
import {DropzoneArea} from "mui-file-dropzone";

interface FileUploadProps {
    file: File | null;
    setFile: (file: File | null) => void;
    fileObjects: File[];
    setFileObjects: (files: File[]) => void;
    selectedRules: RegexRule[];
    setLoading: (loading: boolean) => void;
    setAnonymizedData: (data: AnonymizedResponse | null) => void;
    setError: (error: string | null) => void;
}

const FileUpload: React.FC<FileUploadProps> = ({
                                                   file,
                                                   setFile,
                                                   fileObjects,
                                                   setFileObjects,
                                                   selectedRules,
                                                   setLoading,
                                                   setAnonymizedData,
                                                   setError,
                                               }) => {


    const handleFileUpload = async () => {
        if (!file || selectedRules.length === 0) return;
        const dictFile = new Blob([JSON.stringify(selectedRules)], {type: 'text/plain'});
        const formData = new FormData();
        formData.append('file', file);
        formData.append('dictionary', dictFile);
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/anonymize/', {
                method: 'POST',
                body: formData,
            });
            if (!response.ok) throw new Error('Error anonymizing file');
            const data: AnonymizedResponse = await response.json();
            setAnonymizedData(data);
            setLoading(false);

        } catch (err) {
            setError('Failed to anonymize file');
            setLoading(false);
        }
    };

    const handleDropZoneChange = (files: File[]) => {
        setFile(files[0]);
        setFileObjects(files);
    }


    return (

        <Grid item xs={8} component={Paper} marginTop={3} padding={2} marginX={"auto"}>
            <DropzoneArea
                onChange={handleDropZoneChange}
                filesLimit={1}
                fileObjects={fileObjects}
                onDelete={() => setFile(null)}
                dropzoneText={"Drag and drop here or click to select a file for anonymization"}
                showFileNames={true}
            />

            {/* Centering the Button */}
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

