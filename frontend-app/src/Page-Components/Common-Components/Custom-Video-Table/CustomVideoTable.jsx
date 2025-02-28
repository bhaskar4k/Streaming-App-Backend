import './CustomVideoTable.css';
import { useState, useEffect } from 'react';
import TestThumbnail from '../../../../public/Images/TestThumbnail.png';
import left_arrow from '../../../../public/Images/left_arrow.svg';
import right_arrow from '../../../../public/Images/right_arrow.svg';


function CustomTable(props) {
    const [video_list, set_video_list] = useState([]);
    const [filtered_video_list, set_filtered_video_list] = useState([]);
    const [total_video, set_total_video] = useState(0);
    const [element_starting_id, set_element_starting_id] = useState(1);
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
    }, [props.video_list, total_video]);


    function apply_pagination(direction){
        if(direction === 1){
            let left_bound = Math.max(1, element_starting_id - max_element_per_page);
            const filteredList = video_list.slice(left_bound - 1, left_bound + max_element_per_page - 1);
            set_filtered_video_list(filteredList);
            set_element_starting_id(left_bound);
        }else{
            let right_bound = Math.min(total_video, element_starting_id + max_element_per_page);
            const filteredList = video_list.slice(right_bound - 1, right_bound + max_element_per_page - 1);
            set_filtered_video_list(filteredList);
            set_element_starting_id(right_bound);
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
                <img src={left_arrow} className='custom_table_pagination_arrow' onClick={() => apply_pagination(1)} />
                <img src={right_arrow} className='custom_table_pagination_arrow' onClick={() => apply_pagination(2)} />

                <span>Results per page : {element_starting_id} - {element_starting_id + Math.min(max_element_per_page, total_video) - 1} of {total_video}</span>

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