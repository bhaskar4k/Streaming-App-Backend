import './CustomUploadedVideoTable.css';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import left_arrow from '../../../../public/Images/left_arrow.svg';
import right_arrow from '../../../../public/Images/right_arrow.svg';
import edit_logo from '../../../../public/Images/edit.svg';
import delete_logo from '../../../../public/Images/delete.svg';
import download from '../../../../public/Images/download.svg';
import { ManageVideoService } from '../../../Service/ManageVideoService';

function CustomUploadedVideoTable(props) {
    const navigate = useNavigate();
    const max_element_per_page = 5;
    const [video_list, set_video_list] = useState([]);
    const [filtered_video_list, set_filtered_video_list] = useState([]);
    const [total_video, set_total_video] = useState(0);
    const [element_starting_id, set_element_starting_id] = useState(0);
    const manageVideoService = new ManageVideoService();

    useEffect(() => {
        set_video_list(props.video_list);
        set_total_video(props.video_list.length);
        set_element_starting_id(0);
    }, [props.video_list]);

    useEffect(() => {
        update_filtered_list();
    }, [element_starting_id, max_element_per_page, video_list]);

    function update_filtered_list() {
        const filteredList = video_list.slice(element_starting_id, element_starting_id + max_element_per_page);
        set_filtered_video_list(filteredList);
    }

    function apply_pagination(direction) {
        let new_starting_id = element_starting_id;

        if (direction === 1) {
            new_starting_id = Math.max(0, element_starting_id - max_element_per_page);
        } else {
            if (element_starting_id + max_element_per_page < total_video) {
                new_starting_id = element_starting_id + max_element_per_page;
            }
        }

        set_element_starting_id(new_starting_id);
    }

    function edit_video(t_video_info_id, guid, video_title, thumbnail, video_description, is_public, uploaded_at, processing_status) {
        console.log(thumbnail)
        navigate(`/manage/uploaded-video/edit`, {
            state: {
                t_video_info_id: t_video_info_id,
                guid: guid,
                video_title: video_title,
                video_description: video_description,
                is_public: is_public,
                uploaded_at: uploaded_at,
                processing_status: processing_status,
                old_thumbnail: thumbnail
            }
        });
    }

    async function delete_video(t_video_info_id) {
        try {
            let response = await manageVideoService.DoDeleteVideo(t_video_info_id);

            if (response.status === 200) {
                location.reload();
            }
        } catch (error) {
            console.error('Delete failed:', error);
        }
    }

    async function download_video(guid) {
        try {
            const response = await manageVideoService.DoDownloadVideo(guid);

            const url = window.URL.createObjectURL(new Blob([response.data]));

            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', guid + ".mp4");
            document.body.appendChild(link);

            link.click();

            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Download failed:', error);
        }
    }

    return (
        <>
            <table className='custom_table'>
                <thead className='custom_tablehead'>
                    <tr className='custom_tablehead_row'>
                        {props.column_name.map((header, index) => (
                            <th className='custom_tablehead_cell' key={index}>{header}</th>
                        ))}
                    </tr>
                </thead>

                <tbody className='custom_tablebody'>
                    {filtered_video_list.map((row, index) => (
                        <tr className='custom_tablebody_row' key={index}>
                            <td className='custom_tablebody_cell video_cell'>
                                <img src={row.thumbnail ? row.thumbnail : null} className='custom_table_video_thumbnail' />
                            </td>
                            <td className='custom_tablebody_cell video_title_cell'>{row.video_title}</td>
                            <td className='custom_tablebody_cell video_visibility_cell'>{row.visibility}</td>
                            <td className='custom_tablebody_cell video_uploaded_at_cell'>{row.uploaded_at}</td>
                            <td className='custom_tablebody_cell video_processing_status_cell'>{row.processing_status}</td>
                            <td className='custom_tablebody_cell video_action_cell'>
                                <img src={download} title='Download Video' onClick={() => download_video(row.guid)} className='download_logo' />
                                <img src={edit_logo} title='Edit Video' onClick={() => edit_video(row.t_video_info_id, row.guid, row.video_title, row.thumbnail, row.video_description, row.is_public, row.uploaded_at, row.processing_status)} className='edit_logo' />
                                <img src={delete_logo} title='Delete Video' onClick={() => delete_video(row.t_video_info_id)} className='delete_logo' />
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div className='pagination'>
                <img src={left_arrow} className='custom_table_pagination_arrow' onClick={() => apply_pagination(1)} />
                <img src={right_arrow} className='custom_table_pagination_arrow' onClick={() => apply_pagination(2)} />

                <span>Showing results: {Math.min(element_starting_id + 1, total_video)} - {Math.min(element_starting_id + max_element_per_page, total_video)} of {total_video}</span>
            </div>
        </>
    );
}

export default CustomUploadedVideoTable;
