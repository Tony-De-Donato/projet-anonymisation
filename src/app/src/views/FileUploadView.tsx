import React, {useEffect, useRef, useState} from 'react';
import FileUpload from '../components/FileUpload';
import AnonymizedContent from '../components/AnonymizedContent';
import FileLoadingAndError from "../components/FileLoadingAndError";
import {Grid} from "@mui/material";
import Header from "../components/Header";
import {useSelector} from "react-redux";


const FileUploadView: React.FC = () => {


    const fileUploadRef = useRef<HTMLDivElement | null>(null);
    const anonymizedContentRef = useRef<HTMLDivElement | null>(null);


    const [currentIndex, setCurrentIndex] = useState<number>(0);


    const [isHoveringTable, setIsHoveringTable] = useState<boolean>(false);

    const handleScroll = (event: WheelEvent) => {
        if (isHoveringTable) return;
        event.preventDefault();
        const direction = event.deltaY > 0 ? 'down' : 'up';
        const refs = [fileUploadRef, anonymizedContentRef];
        const nextIndex = direction === 'down'
            ? Math.min(currentIndex + 1, refs.length - 1)
            : Math.max(currentIndex - 1, 0);
        const nextRef = refs[nextIndex];
        if (nextRef?.current) {
            nextRef.current.scrollIntoView({behavior: 'smooth'});
            setCurrentIndex(nextIndex);
        }
    };

    useEffect(() => {
        const scrollContainer = document.getElementById('scrollContainer');
        if (scrollContainer) {
            scrollContainer.addEventListener('wheel', handleScroll, {passive: false});
        }
        return () => {
            if (scrollContainer) {
                scrollContainer.removeEventListener('wheel', handleScroll);
            }
        };
    }, [currentIndex, handleScroll, isHoveringTable]);

    const fileLoading = useSelector((state: any) => state.files.loading);
    const errorFileDownload = useSelector((state: any) => state.files.error);

    return (
        <div id={"scrollContainer"}>
            {fileLoading || errorFileDownload ? (
                <FileLoadingAndError/>
            ) : null}
            <Grid container>

                <Grid item xs={12} ref={fileUploadRef}>
                    <Header/>
                    <FileUpload/>
                </Grid>
                <Grid item xs={12} ref={anonymizedContentRef}>
                    <AnonymizedContent setIsHoveringTable={setIsHoveringTable}/>
                </Grid>
            </Grid>
        </div>
    );
};

export default FileUploadView;
