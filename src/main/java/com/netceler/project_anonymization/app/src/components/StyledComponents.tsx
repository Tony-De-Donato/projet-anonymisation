import {styled} from '@mui/material/styles';
import {TableCell, TableRow} from '@mui/material';

export const StyledTableRow = styled(TableRow)({
    '&:nth-of-type(odd)': {
        backgroundColor: 'rgba(0, 0, 0, 0.04)',
    },
});

export const StyledTableCellHeader = styled(TableCell)({
    backgroundColor: '#2d3c5e',
    fontSize: "large",
    color: 'white',

    'first-child': {
        borderTopLeftRadius: '5px',
    },
    'last-child': {
        borderTopRightRadius: '5px',
    },
});
