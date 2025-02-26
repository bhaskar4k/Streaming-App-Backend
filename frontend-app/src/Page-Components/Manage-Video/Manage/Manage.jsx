import './Manage.css';
import { useState, useEffect } from 'react';
import { ManageVideoService } from '../../../Service/ManageVideoService';
import { DateFormat } from '../../../Common/CommonConts';

import * as React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Paper from '@mui/material/Paper';


function Manage() {
    const [uploaded_video_list, set_uploaded_video_list] = useState([]);
    const paginationModel = { page: 0, pageSize: 5 };
    const manageVideoService = new ManageVideoService();

    const columns_name = [
        { field: 'video_title', headerName: 'Title', flex: 1, headerAlign: 'center', align: 'center' },
        { field: 'visibility', headerName: 'Visibility', flex: 1, headerAlign: 'center', align: 'center' },
        { field: 'uploaded_at', headerName: 'Uploaded At', type: 'number', flex: 1, headerAlign: 'center', align: 'center' },
        { field: 'processing_status', headerName: 'Processing Status', type: 'number', flex: 1, headerAlign: 'center', align: 'center' },
    ];

    useEffect(() => {
        getVideoInfo();
    }, []);

    async function getVideoInfo() {
        try {
            let response = await manageVideoService.GetUploadeVideoList();
            console.log(response.data);

            let data = response.data;

            let rows = data.map((item) => {
                return {
                    id: item.t_video_info_id,
                    visibility: (item.is_public === 1) ? "Public" : "Private",
                    video_title: item.video_title,
                    uploaded_at: new Intl.DateTimeFormat('en-US', DateFormat).format(new Date(item.trans_datetime)),
                    processing_status: (item.processing_status === 1) ? "In Queue" :
                        (item.processing_status === 2) ? "Processing" :
                            (item.processing_status === 3) ? "Processed" : "Processing Failed"
                };
            });

            set_uploaded_video_list(rows);
        } catch (error) {
            console.error("Error:", error);
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Failed to fetch video info.");
        }
    }

    return (
        <>
            <div id="manage_video_container">
                <h1>Manage Video</h1>

                <Paper sx={{ maxHeight: 700, width: '100%' }}>
                    <DataGrid
                        rows={uploaded_video_list}
                        columns={columns_name}
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