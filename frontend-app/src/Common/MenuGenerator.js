export function GenerateMenu(res, iconMap) {
    let output = "";

    res.forEach((parent) => {
        if (parent.parent_id <= 0) {
            if (parent.parent_id === 0) {
                output += `<div class="a-menu-item" onclick="toggleSubmenu(${parent.id},${parent.parent_id},'${parent.route_name}')">
                                <div class="menu-item">
                                    <img src="${iconMap[parent.menu_icon]}" class="menu-icons" alt="${parent.menu_icon}" />
                                    <h4 class="menu-item-text" id="${parent.menu_name_id}">${parent.menu_name}</h4>
                                </div>
                            </div>`;
            }

            if (parent.parent_id === -1) {
                output += `<div class="a-menu-item" onclick="toggleSubmenu(${parent.id},${parent.parent_id},'${parent.route_name}')">
                                <div class="menu-item">
                                    <img src="${iconMap[parent.menu_icon]}" class="menu-icons" alt="${parent.menu_icon}" />
                                    <h4 class="menu-item-text" id="${parent.menu_name_id}">${parent.menu_name}</h4>
                                    
                                </div>    
                                <img src="${iconMap.down_arrow}" alt="${iconMap.down_arrow}" class="a-menu-item-subitem-toggle" id="toggle-${parent.id}"/>                            
                            </div>`;
            }

            output += `<div class="submenu" id="submenu-${parent.id}">`;

            res.forEach((child) => {
                if (child.parent_id === parent.id) {
                    output += `<div class="a-menu-item-child" onclick="navigateTo('${child.route_name}')">
                                    <div class="menu-item">
                                        <img src="${iconMap[child.menu_icon]}" class="menu-icons" alt="${child.menu_icon}" />
                                        <h4 class="menu-item-text" id="${child.menu_name_id}">${child.menu_name}</h4>
                                    </div>
                               </div>`;
                }
            });

            output += `</div>`;
        }
    });

    return output;
}