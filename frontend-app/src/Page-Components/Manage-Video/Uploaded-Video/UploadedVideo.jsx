import * as React from 'react';
import './UploadedVideo.css';
import { useState, useEffect } from 'react';
import CustomTable from '../../Common-Components/Custom-Video-Table/CustomVideoTable';
import { ManageVideoService } from '../../../Service/ManageVideoService';
import { DateFormat } from '../../../Common/CommonConts';


function Manage() {
    const [uploaded_video_list, set_uploaded_video_list] = useState([]);
    const [column_name_mapper, set_column_name_mapper] = useState([]);
    const [column_name, set_column_name] = useState([]);
    const manageVideoService = new ManageVideoService();

    useEffect(() => {
        getVideoInfo();
    }, []);

    async function getVideoInfo() {
        try {
            let response = await manageVideoService.GetUploadeVideoList();
            set_column_name_mapper(["video", "video_title", "visibility", "uploaded_at", "processing_status", "action"]);
            set_column_name(["Video", "Video Title", "Visibility", "Uploaded At", "Processing Status", "Action"]);

            console.log(response.data)

            let i=1;
            let rows = response.data.map((item) => {
                return {
                    t_video_info_id: item.t_video_info_id,
                    thumbnail: (item.thumbnail_uploaded === 1 ? `data:image/jpeg;base64,${item.base64EncodedImage}` : null),
                    t_video_info_id: item.t_video_info_id,
                    is_public: item.is_public,
                    visibility: (item.is_public === 1) ? "Public" : "Private",
                    video_title: item.video_title,
                    video_description: item.video_description,
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
                <h1 className='page_title'>Manage Video</h1>

                <CustomTable video_list={uploaded_video_list} column_name_mapper={column_name_mapper} column_name={column_name}/>
            </div>
        </>
    );
}

export default Manage;