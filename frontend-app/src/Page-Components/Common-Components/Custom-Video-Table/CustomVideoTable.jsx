import './CustomVideoTable.css';
import { useState, useEffect } from 'react';
import TestThumbnail from '../../../../public/Images/TestThumbnail.png';


function CustomTable(props) {
    const [video_list, set_video_list] = useState([]);

    useEffect(() => {
        set_video_list(props.video_list)
        console.log(props.video_list)
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
                    {props.video_list.map((row, index) => (
                        <tr className='custom_tablebody_row' key={index}>
                            <td className='custom_tablebody_cell video_cell'><img src={TestThumbnail} className='custom_table_video_thumbnail'/></td>
                            <td className='custom_tablebody_cell video_title_cell'>{row.video_title}</td>
                            <td className='custom_tablebody_cell video_visibility_cell'>{row.visibility}</td>
                            <td className='custom_tablebody_cell video_uploaded_at_cell'>{row.uploaded_at}</td>
                            <td className='custom_tablebody_cell video_processing_status_cell'>{row.processing_status}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </>
    );
}

export default CustomTable;