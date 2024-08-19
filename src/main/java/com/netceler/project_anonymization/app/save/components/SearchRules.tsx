import React from 'react';
import {
    Alert,
    Button,
    Checkbox,
    FormControlLabel,
    Grid,
    MenuItem,
    Paper,
    Select,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import {RegexRule} from '../interfaces/RegexRule';
import {StyledTableCellHeader, StyledTableRow} from "./StyledComponents";
import CircularProgress from '@mui/material/CircularProgress';

interface SearchRulesProps {
    rules: RegexRule[];
    setRules: React.Dispatch<React.SetStateAction<RegexRule[]>>;
    selectedRules: RegexRule[];
    setSelectedRules: React.Dispatch<React.SetStateAction<RegexRule[]>>;
    searchTerm: string;
    setSearchTerm: React.Dispatch<React.SetStateAction<string>>;
    searchType: string;
    setSearchType: React.Dispatch<React.SetStateAction<string>>;
    accurateChecked: boolean;
    setAccurateChecked: React.Dispatch<React.SetStateAction<boolean>>;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

const SearchRules: React.FC<SearchRulesProps> = ({
                                                     rules,
                                                     setRules,
                                                     selectedRules,
                                                     setSelectedRules,
                                                     searchTerm,
                                                     setSearchTerm,
                                                     searchType,
                                                     setSearchType,
                                                     accurateChecked,
                                                     setAccurateChecked,
                                                     loading,
                                                     setLoading,
                                                     error,
                                                     setError,
                                                 }) => {

    const searchRules = async (url: string) => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(url);
            // if (!response.ok) throw new Error('Error fetching rules');
            const data: RegexRule[] = await response.json();
            setRules(data);
            setLoading(false);
            if (data === undefined || data.length === 0) {
                setError('No rules found with the given search criteria');
            }
        } catch (err) {
            setError('Failed to fetch rules');
            setLoading(false);
        }
    };

    const searchRulesFromSearchBar = async () => {
        const url = `http://localhost:8080/${
            searchType === 'rulename' ? `getDictByRuleName/${searchTerm}/${accurateChecked}` : `getDictByFileName/${searchTerm}/${accurateChecked}`
        }`;
        searchRules(url);
    };

    const handleRuleSelection = (rule: RegexRule) => {
        if (selectedRules.includes(rule)) {
            setSelectedRules((prev) => prev.filter((r) => r !== rule));
        } else {
            setSelectedRules((prev) => [...prev, rule]);
        }
    };

    const handleSelectAll = () => {
        for (let i = 0; i < rules.length; i++) {
            if (!selectedRules.includes(rules[i])) {
                setSelectedRules((prev) => [...prev, rules[i]]);
            }
        }
    }

    return (
        <Grid item xs={12} padding={2} component={Paper} minHeight={350}>
            <Grid container spacing={2}>

                {/* Search Rules Section */}
                <Grid item xs={12} md={6}>
                    <Grid container spacing={2}>

                        {/* Search Rules Inputs */}
                        <Grid item xs={12} padding={2}>
                            <Typography textAlign={"left"} variant="h6">Search Rules</Typography>

                            <Grid container spacing={2} marginTop={1} alignItems={"center"}>
                                <Grid item xs={12} md={6}>
                                    <TextField
                                        label="Search by name or filename"
                                        variant="outlined"
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <Select
                                        labelId="search-type-label"
                                        id="search-type"
                                        value={searchType}
                                        onChange={(e) => setSearchType(e.target.value)}
                                    >
                                        <MenuItem value="rulename">Rule name</MenuItem>
                                        <MenuItem value="filename">File name</MenuItem>
                                    </Select>
                                    <FormControlLabel
                                        style={{marginLeft: '3%'}}
                                        control={
                                            <Checkbox
                                                checked={accurateChecked}
                                                onChange={(e) => setAccurateChecked(e.target.checked)}
                                            />
                                        }
                                        label="Accurate Search"
                                    />
                                </Grid>

                            </Grid>
                        </Grid>

                        {/* Search Rules Buttons */}
                        <Grid item xs={12} padding={2}>
                            <Grid container spacing={8} paddingInline={2}>
                                <Grid item xs={12} sm={4}>
                                    <Button
                                        variant="contained"
                                        onClick={searchRulesFromSearchBar}
                                        fullWidth
                                        style={{whiteSpace: 'nowrap'}}
                                    >
                                        Search
                                    </Button>
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <Button
                                        variant="outlined"
                                        onClick={() => searchRules('http://localhost:8080/getAllDict')}
                                        fullWidth
                                        style={{whiteSpace: 'nowrap'}}
                                    >
                                        Get All Rules
                                    </Button>
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <Button
                                        variant="outlined"
                                        onClick={() => searchRules('http://localhost:8080/getAllDefaultDict')}
                                        fullWidth
                                        style={{whiteSpace: 'nowrap'}}
                                    >
                                        Get Default Rules
                                    </Button>
                                </Grid>
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>

                {/* Available Rules */}
                <Grid item xs={12} md={6}>
                    {loading && <CircularProgress/>}
                    {error && <Alert severity="error">{error}</Alert>}

                    {/* Available Rules Result */}
                    <TableContainer component={Paper} sx={{
                        maxHeight: 300,
                        overflow: 'auto',
                        '&::-webkit-scrollbar': {
                            display: 'none',
                        },
                        '-ms-overflow-style': 'none',
                        'scrollbar-width': 'none',
                    }}>
                        <Table stickyHeader={true}>
                            <TableHead>
                                <TableRow>
                                    <StyledTableCellHeader>Name</StyledTableCellHeader>
                                    <StyledTableCellHeader>Regexp</StyledTableCellHeader>
                                    <StyledTableCellHeader>Replacement</StyledTableCellHeader>
                                    <StyledTableCellHeader align={"right"}>
                                        <Button
                                            variant="contained"
                                            color="success"
                                            onClick={handleSelectAll}
                                            style={{whiteSpace: 'nowrap', minWidth: '100px'}}
                                        >
                                            Select All
                                        </Button>
                                    </StyledTableCellHeader>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {rules.map((rule, index) => (
                                    <StyledTableRow key={index}>
                                        <TableCell>{rule.name}</TableCell>
                                        <TableCell>{rule.regexp}</TableCell>
                                        <TableCell>{rule.replacement}</TableCell>
                                        <TableCell align={"right"}>
                                            {selectedRules.includes(rule) ?
                                                <Button
                                                    variant="outlined"
                                                    color="error"
                                                    onClick={() => handleRuleSelection(rule)}
                                                >
                                                    -
                                                </Button>
                                                :
                                                <Button
                                                    variant="outlined"
                                                    color="success"
                                                    onClick={() => handleRuleSelection(rule)}
                                                >
                                                    +
                                                </Button>
                                            }
                                        </TableCell>
                                    </StyledTableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Grid>
            </Grid>
        </Grid>
    );
};

export default SearchRules;
