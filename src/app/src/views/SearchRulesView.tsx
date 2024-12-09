import React, {useEffect, useRef, useState} from 'react';
import {Grid} from '@mui/material';

import {RegexRule} from '../interfaces/RegexRule';
import Header from '../components/Header';
import SearchRules from "../components/SearchRules";
import SelectedRules from "../components/SelectedRules";


const SearchRulesView: React.FC = () => {


    const [searchLoading, setSearchLoading] = useState<boolean>(false);
    const [searchError, setSearchError] = useState<string | null>(null);
    const [errorDictUpload, setErrorDictUpload] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [searchType, setSearchType] = useState<string>('rulename');
    const [accurateChecked, setAccurateChecked] = useState<boolean>(false);
    const [editingRule, setEditingRule] = useState<RegexRule | null>(null);
    const [newRule, setNewRule] = useState<RegexRule>({name: '', regexp: '', replacement: ''});


    const searchRulesRef = useRef<HTMLDivElement | null>(null);
    const selectedRulesRef = useRef<HTMLDivElement | null>(null);
    const [currentIndex, setCurrentIndex] = useState<number>(0);
    const [isHoveringTable, setIsHoveringTable] = useState<boolean>(false);

    const handleScroll = (event: WheelEvent) => {
        if (isHoveringTable) return;
        event.preventDefault();
        const direction = event.deltaY > 0 ? 'down' : 'up';
        const refs = [searchRulesRef, selectedRulesRef];
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
            <Grid container>
                <Grid item xs={12} ref={searchRulesRef}>
                    <Header/>
                    <SearchRules
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
                <Grid item xs={12} paddingBottom={4} ref={selectedRulesRef}>
                    <SelectedRules
                        errorDictUpload={errorDictUpload}
                        setErrorDictUpload={setErrorDictUpload}
                        editingRule={editingRule}
                        setEditingRule={setEditingRule}
                        newRule={newRule}
                        setNewRule={setNewRule}
                        setIsHoveringTable={setIsHoveringTable}
                    />
                </Grid>
            </Grid>
        </div>
    );
};

export default SearchRulesView;