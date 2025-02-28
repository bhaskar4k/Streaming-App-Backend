import './CustomVideoTable.css';
import { useState, useEffect } from 'react';
import TestThumbnail from '../../../../public/Images/TestThumbnail.png';


function CustomTable(props) {
    const [video_list, set_video_list] = useState([]);
    const [filtered_video_list, set_filtered_video_list] = useState([]);
    const [total_video, set_total_video] = useState(0);
    const [max_element_per_page, set_max_element_per_page] = useState(5);

    function update_max_element_per_page() {
        const selectElement = document.getElementById('pagination_limit');
        set_max_element_per_page(selectElement.value);

        const filteredList = video_list.slice(0, max_element_per_page);
        set_filtered_video_list(filteredList);
    }

    useEffect(() => {
        set_video_list(props.video_list);
        set_filtered_video_list(props.video_list);
        set_total_video(props.video_list.length);

        update_max_element_per_page();
    });



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
                            <td className='custom_tablebody_cell video_cell'><img src={TestThumbnail} className='custom_table_video_thumbnail' /></td>
                            <td className='custom_tablebody_cell video_title_cell'>{row.video_title}</td>
                            <td className='custom_tablebody_cell video_visibility_cell'>{row.visibility}</td>
                            <td className='custom_tablebody_cell video_uploaded_at_cell'>{row.uploaded_at}</td>
                            <td className='custom_tablebody_cell video_processing_status_cell'>{row.processing_status}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div className='pagination'>
                <span>Results per page : 1 - {Math.min(max_element_per_page, total_video)} of {total_video}</span>
                <div className="select-dropdown">
                    <select id="pagination_limit" onClick={update_max_element_per_page}>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                    </select>
                </div>
            </div>
        </>
    );
}

export default CustomTable;