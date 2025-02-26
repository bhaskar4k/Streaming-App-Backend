import './Manage.css';
import { useState, useEffect } from 'react';

import * as React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Paper from '@mui/material/Paper';

const paginationModel = { page: 0, pageSize: 5 };

function Manage() {
    const columns = [
        { field: 'firstName', headerName: 'First name', flex: 1, headerAlign: 'center', align: 'center' },
        { field: 'lastName', headerName: 'Last name', flex: 1, headerAlign: 'center', align: 'center' },
        { field: 'age', headerName: 'Age', type: 'number', flex: 1, headerAlign: 'center', align: 'center' },
    ];

    const rows = [
        { id: 1, lastName: 'Snow', firstName: 'Jon', age: 35 },
        { id: 2, lastName: 'Lannister', firstName: 'Cersei', age: 42 },
        { id: 3, lastName: 'Lannister', firstName: 'Jaime', age: 45 },
        { id: 4, lastName: 'Stark', firstName: 'Arya', age: 16 },
        { id: 5, lastName: 'Targaryen', firstName: 'Daenerys', age: null },
        { id: 6, lastName: 'Melisandre', firstName: null, age: 150 },
        { id: 7, lastName: 'Clifford', firstName: 'Ferrara', age: 44 },
        { id: 8, lastName: 'Frances', firstName: 'Rossini', age: 36 },
        { id: 9, lastName: 'Roxie', firstName: 'Harvey', age: 65 },
    ];

    return (
        <>
            <div id="manage_video_container">
                <h1>Manage Video</h1>

                <Paper sx={{ maxHeight: 700, width: '100%' }}>
                    <DataGrid
                        rows={rows}
                        columns={columns}
                        initialState={{ pagination: { paginationModel } }}
                        pageSizeOptions={[5, 10, 20, 50, 100]}
                        checkboxSelection
                        sx={{
                            border: 0,
                            '& .MuiTablePagination-root': {
                                alignItems: 'center',
                                
                            },
                            '& .MuiSelect-select': {
                                backgroundColor: '#e0e0e0',
                                borderRadius: '5px',
                                padding: '6px 12px',
                                display: 'flex',
                                alignItems: 'center',
                            },
                            '& .MuiSelect-icon': {
                                top: '50%',
                                transform: 'translateY(-50%)',
                            },
                            '& .MuiTablePagination-select': {
                                paddingTop: '6px',
                                paddingBottom: '6px',
                            }
                        }}
                    />
                </Paper>
            </div>
        </>
    );
}

export default Manage;