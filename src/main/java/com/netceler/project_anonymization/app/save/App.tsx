import React, {useState} from 'react';
import {Grid,} from '@mui/material';

import {AnonymizedResponse} from './interfaces/AnonymizedResponse';
import {RegexRule} from './interfaces/RegexRule';
import Header from './components/Header';
import SearchRules from "./components/SearchRules";
import SelectedRules from "./components/SelectedRules";
import FileUpload from "./components/FileUpload";
import AnonymizedContent from "./components/AnonymizedContent";

const App: React.FC = () => {
    const [rules, setRules] = useState<RegexRule[]>([]);
    const [selectedRules, setSelectedRules] = useState<RegexRule[]>([]);
    const [file, setFile] = useState<File | null>(null);
    const [fileObjects, setFileObjects] = useState<File[]>([]);
    const [anonymizedData, setAnonymizedData] = useState<AnonymizedResponse | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [errorDictUpload, setErrorDictUpload] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [searchType, setSearchType] = useState<string>('rulename');
    const [accurateChecked, setAccurateChecked] = useState<boolean>(false);

    const [editingRule, setEditingRule] = useState<RegexRule | null>(null);
    const [newRule, setNewRule] = useState<RegexRule>({name: '', regexp: '', replacement: ''});

    return (
        <Grid container>

            {/* Header */}
            <Header/>

            {/* Main Content */}
            <Grid item>
                {/* Rules Research */}
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
                    loading={loading}
                    setLoading={setLoading}
                    error={error}
                    setError={setError}
                />

                {/* Selected Rules */}
                <SelectedRules
                    selectedRules={selectedRules}
                    setSelectedRules={setSelectedRules}
                    errorDictUpload={errorDictUpload}
                    setErrorDictUpload={setErrorDictUpload}
                    editingRule={editingRule}
                    setEditingRule={setEditingRule}
                    newRule={newRule}
                    setNewRule={setNewRule}
                />

                {/* File Upload for Anonymization */}
                <FileUpload file={file} setFile={setFile}
                            fileObjects={fileObjects}
                            setFileObjects={setFileObjects}
                            selectedRules={selectedRules}
                            setLoading={setLoading}
                            setAnonymizedData={setAnonymizedData}
                            setError={setError}
                />

                {/* Anonymized Content */}
                <AnonymizedContent anonymizedData={anonymizedData}/>

            </Grid>
        </Grid>
    );
};

export default App;
