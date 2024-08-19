import React, {useEffect, useRef, useState} from 'react';
import {Grid} from '@mui/material';

import {AnonymizedResponse} from './interfaces/AnonymizedResponse';
import {RegexRule} from './interfaces/RegexRule';
import Header from './components/Header';
import SearchRules from "./components/SearchRules";
import SelectedRules from "./components/SelectedRules";
import FileUpload from "./components/FileUpload";
import AnonymizedContent from "./components/AnonymizedContent";
import FileLoadingAndError from "./components/FileLoadingAndError";

const App: React.FC = () => {
    const [rules, setRules] = useState<RegexRule[]>([]);
    const [selectedRules, setSelectedRules] = useState<RegexRule[]>([]);
    const [file, setFile] = useState<File | null>(null);
    const [fileObjects, setFileObjects] = useState<File[]>([]);
    const [anonymizedData, setAnonymizedData] = useState<AnonymizedResponse | null>(null);
    const [searchLoading, setSearchLoading] = useState<boolean>(false);
    const [searchError, setSearchError] = useState<string | null>(null);
    const [errorDictUpload, setErrorDictUpload] = useState<string | null>(null);
    const [errorFileDownload, setErrorFileDownload] = useState<string | null>(null);
    const [fileLoading, setFileLoading] = useState<boolean>(false);
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [searchType, setSearchType] = useState<string>('rulename');
    const [accurateChecked, setAccurateChecked] = useState<boolean>(false);
    const [editingRule, setEditingRule] = useState<RegexRule | null>(null);
    const [newRule, setNewRule] = useState<RegexRule>({name: '', regexp: '', replacement: ''});

    // Create refs for each section
    const searchRulesRef = useRef<HTMLDivElement | null>(null);
    const selectedRulesRef = useRef<HTMLDivElement | null>(null);
    const fileUploadRef = useRef<HTMLDivElement | null>(null);
    const anonymizedContentRef = useRef<HTMLDivElement | null>(null);


    const [currentIndex, setCurrentIndex] = useState<number>(0);


    const [isHoveringTable, setIsHoveringTable] = useState<boolean>(false);

    const handleScroll = (event: WheelEvent) => {
        if (isHoveringTable) return;


        event.preventDefault();
        const direction = event.deltaY > 0 ? 'down' : 'up';

        const refs = [searchRulesRef, selectedRulesRef, fileUploadRef, anonymizedContentRef];


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


    return (
        <div id="scrollContainer">
            {fileLoading || errorFileDownload ? (
                <FileLoadingAndError
                    isLoading={fileLoading}
                    error={errorFileDownload}
                />
            ) : null}
            <Grid container>
                <Grid item xs={12} ref={searchRulesRef}>
                    <Header/>
                    <SearchRules
                        rules={rules}
                        setRules={setRules}
                        selectedRules={selectedRules}
                        setSelectedRules={setSelectedRules}
                        searchTerm={searchTerm}
                        setSearchTerm={setSearchTerm}
                        searchType={searchType}
                        setSearchType={setSearchType}
                        accurateChecked={accurateChecked}
                        setAccurateChecked={setAccurateChecked}
                        loading={searchLoading}
                        setLoading={setSearchLoading}
                        error={searchError}
                        setError={setSearchError}
                        setIsHoveringTable={setIsHoveringTable}
                    />
                </Grid>
                <Grid item xs={12} ref={selectedRulesRef}>
                    <SelectedRules
                        selectedRules={selectedRules}
                        setSelectedRules={setSelectedRules}
                        errorDictUpload={errorDictUpload}
                        setErrorDictUpload={setErrorDictUpload}
                        editingRule={editingRule}
                        setEditingRule={setEditingRule}
                        newRule={newRule}
                        setNewRule={setNewRule}
                        setIsHoveringTable={setIsHoveringTable}
                    />
                </Grid>
                <Grid item xs={12} ref={fileUploadRef}>
                    <FileUpload
                        file={file}
                        setFile={setFile}
                        fileObjects={fileObjects}
                        setFileObjects={setFileObjects}
                        selectedRules={selectedRules}
                        setLoading={setFileLoading}
                        setAnonymizedData={setAnonymizedData}
                        setError={setErrorFileDownload}
                    />
                </Grid>
                <Grid item xs={12} ref={anonymizedContentRef}>
                    <AnonymizedContent
                        anonymizedData={anonymizedData}
                        setError={setErrorFileDownload}
                        setLoading={setFileLoading}
                    />
                </Grid>
            </Grid>
        </div>
    );
};

export default App;
