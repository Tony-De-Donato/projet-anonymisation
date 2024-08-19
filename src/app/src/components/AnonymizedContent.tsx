import React, {useEffect, useRef} from 'react';
import {saveAs} from 'file-saver';
import {Button, Grid, Paper, Typography} from '@mui/material';
import VerifiedUserRoundedIcon from '@mui/icons-material/VerifiedUserRounded';
import {AnonymizedResponse} from '../interfaces/AnonymizedResponse';
import apiUrl from "../constant/apiUrl";

interface AnonymizedContentProps {
    anonymizedData: AnonymizedResponse | null;
    setLoading: (loading: boolean) => void;
    setError: (error: string | null) => void;
}

const FileUpload: React.FC<AnonymizedContentProps> = ({
                                                          anonymizedData,
                                                          setLoading,
                                                          setError
                                                      }) => {
    const contentRef = useRef<HTMLPreElement | null>(null);

    const downloadFile = (content: string, filename: string) => {
        const file = new Blob([content], {type: 'text/plain'});
        saveAs(file, filename);
    };

    useEffect(() => {
        if (anonymizedData && contentRef.current) {
            contentRef.current.scrollIntoView({behavior: 'smooth'});
        }
    }, [anonymizedData]);

    const downloadDictionary = async () => {
        if (!anonymizedData) return;

        setLoading(true);
        try {
            const response = await fetch(`${apiUrl}getDictFile/` + anonymizedData.dict, {
                method: 'GET'
            });
            if (!response.ok) throw new Error('Error downloading dictionary');
            const data = await response.blob();
            saveAs(data, anonymizedData.dict);
            setLoading(false);
        } catch (err) {
            setError('Failed to download dictionary');
            setLoading(false);
        }
    };


    const renderAnonymizedContent = () => {
        if (!anonymizedData) return null;

        const lines = anonymizedData.content.split('\n');
        const totalLines = lines.length;
        lines.splice(50);
        if (totalLines > 50) {
            lines.push(`\n\n... and ${totalLines - 50} more lines`);
        }

        return (
            <pre
                ref={contentRef}
                style={{
                    maxHeight: '60vh',
                    overflowY: 'auto',
                    backgroundColor: '#e4e4e4',
                    padding: '1em',
                    borderRadius: '4px',
                    fontFamily: 'monospace',
                    fontSize: '14px'
                }}
            >
                {lines.join('\n')}
            </pre>
        );
    };

    return (
        <Grid>

            {anonymizedData && (
                <Grid item xs={12} component={Paper} marginY={4} padding={2}>
                    <Typography variant="h6">Anonymized Content</Typography>
                    {renderAnonymizedContent()}
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => downloadFile(anonymizedData.content, anonymizedData.fileName)}
                        style={{marginRight: '2em'}}
                    >
                        <VerifiedUserRoundedIcon style={{marginRight: '0.5em'}}/>
                        Download Anonymized File
                    </Button>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={() => downloadDictionary()}
                    >
                        Download Dictionary
                    </Button>
                </Grid>
            )}
        </Grid>
    );
};

export default FileUpload;
