import React from 'react';
import {
    Alert,
    Button,
    Grid,
    Paper,
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
import {MuiFileInput} from "mui-file-input";

interface SelectedRulesProps {
    selectedRules: RegexRule[];
    setSelectedRules: React.Dispatch<React.SetStateAction<RegexRule[]>>;
    errorDictUpload: string | null;
    setErrorDictUpload: (error: string | null) => void;
    editingRule: RegexRule | null;
    setEditingRule: (rule: RegexRule | null) => void;
    newRule: RegexRule;
    setNewRule: React.Dispatch<React.SetStateAction<RegexRule>>;
}


const SelectedRules: React.FC<SelectedRulesProps> = ({
                                                         selectedRules,
                                                         setSelectedRules,
                                                         errorDictUpload,
                                                         setErrorDictUpload,
                                                         editingRule,
                                                         setEditingRule,
                                                         newRule,
                                                         setNewRule,
                                                     }) => {

    const handleRuleSelection = (rule: RegexRule) => {
        if (selectedRules.includes(rule)) {
            setSelectedRules((prev) => prev.filter((r: RegexRule) => r !== rule));
        } else {
            setSelectedRules((prev) => [...prev, rule]);
        }
    };


    const handleDictFileUpload = (file: File | null) => {
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const content = e.target?.result as string;
                    const parsedRules: RegexRule[] = JSON.parse(content);
                    setSelectedRules((prev) => [...prev, ...parsedRules]);
                    setErrorDictUpload(null);
                } catch (err) {
                    setErrorDictUpload('Failed to parse dict file');
                }
            };
            reader.readAsText(file);
        }
    };

    const handleAddOrUpdateRule = () => {
        if (editingRule) {
            setSelectedRules((prev) =>
                prev.map((rule: RegexRule) => (rule === editingRule ? newRule : rule)));
            setEditingRule(null);
        } else {
            setSelectedRules((prev) => [...prev, newRule]);
        }
        setNewRule({name: '', regexp: '', replacement: ''});
    };

    const handleEditRule = (rule: RegexRule) => {
        setEditingRule(rule);
        setNewRule(rule);
    };

    return (

        <Grid item xs={12} component={Paper} paddingTop={1} marginTop={1} md={12} maxHeight={700}>

            {/*Upload Existing Dictionary File*/}
            <Grid item xs={12} md={12}>
                <Grid container spacing={2} alignItems="center" padding={2}
                      justifyContent="space-between">
                    {/* Typography (Selected Rules) */}
                    <Grid item>
                        <Typography variant="h6">Selected Rules</Typography>
                    </Grid>


                    {/* MuiFileInput */}
                    <Grid item>
                        <Grid item> {errorDictUpload &&
                            <Alert severity={"error"}>{errorDictUpload}</Alert>}</Grid>
                        <MuiFileInput
                            value={null}
                            onChange={handleDictFileUpload}
                            placeholder="Upload Existing Dictionary File"
                            sx={{width: 300}}
                        />
                    </Grid>
                </Grid>
            </Grid>

            {/* Selected Rules Table */}
            <Grid item xs={12} md={12} paddingX={2}>
                <TableContainer sx={{
                    borderRadius: 1,
                    maxHeight: 550,
                    overflow: 'auto',
                    '&::-webkit-scrollbar': {
                        display: 'none',
                    },
                    '-ms-overflow-style': 'none',
                    'scrollbar-width': 'none',
                }}>
                    <Table stickyHeader>
                        <TableHead>
                            {/* First Header Row */}
                            <TableRow>
                                <StyledTableCellHeader>Name</StyledTableCellHeader>
                                <StyledTableCellHeader>Regexp</StyledTableCellHeader>
                                <StyledTableCellHeader>Replacement</StyledTableCellHeader>
                                <StyledTableCellHeader align="right">
                                    <Button
                                        variant="contained"
                                        color="error"
                                        onClick={() => setSelectedRules([])}
                                        style={{whiteSpace: 'nowrap', minWidth: '100px'}}
                                    >
                                        delete all
                                    </Button>
                                </StyledTableCellHeader>
                            </TableRow>
                            {/* Second Header Row */}
                            <TableRow>
                                <TableCell style={{top: 70}}>
                                    <TextField
                                        label="Name"
                                        variant="outlined"
                                        value={newRule.name}
                                        onChange={(e) =>
                                            setNewRule((prev) => ({
                                                ...prev,
                                                name: e.target.value,
                                            }))
                                        }
                                        fullWidth
                                    />
                                </TableCell>
                                <TableCell style={{top: 70}}>
                                    <TextField
                                        label="Regexp"
                                        variant="outlined"
                                        value={newRule.regexp}
                                        onChange={(e) =>
                                            setNewRule((prev) => ({
                                                ...prev,
                                                regexp: e.target.value,
                                            }))
                                        }
                                        fullWidth
                                    />
                                </TableCell>
                                <TableCell style={{top: 70}}>
                                    <TextField
                                        label="Replacement"
                                        variant="outlined"
                                        value={newRule.replacement}
                                        onChange={(e) =>
                                            setNewRule((prev) => ({
                                                ...prev,
                                                replacement: e.target.value,
                                            }))
                                        }
                                        fullWidth
                                    />
                                </TableCell>
                                <TableCell align="right" style={{top: 70}}>
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={handleAddOrUpdateRule}
                                    >
                                        {editingRule ? 'Update Rule' : 'Add Rule'}
                                    </Button>
                                </TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {selectedRules.map((rule, index) => (
                                <StyledTableRow key={index}>
                                    <TableCell>{rule.name}</TableCell>
                                    <TableCell>{rule.regexp}</TableCell>
                                    <TableCell>{rule.replacement}</TableCell>
                                    <TableCell align="right">
                                        <Button
                                            variant="outlined"
                                            color="error"
                                            onClick={() => handleRuleSelection(rule)}
                                        >
                                            -
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            color="primary"
                                            onClick={() => handleEditRule(rule)}
                                            style={{marginLeft: '8px'}}
                                        >
                                            Edit
                                        </Button>
                                    </TableCell>
                                </StyledTableRow>
                            ))}
                        </TableBody>
                    </Table>

                </TableContainer>
            </Grid>

        </Grid>


    );
};

export default SelectedRules;

