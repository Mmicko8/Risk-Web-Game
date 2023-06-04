import axios from "axios";
import {getAvatar} from "./utilsService";

export async function getShopItems() {
    const shopItem = await axios.get(`/api/shopItem/forPlayer`);
    return shopItem.data;
}

export async function buyShopItem(shopItemId: number) {
    return await axios.post(`/api/shopItem/buyItem/${shopItemId}`);
}

export function capitalizeItemCategory(str: string) {
    str = str.toLowerCase();
    str = str.split('_').map((word: string) => {
        return word[0].toUpperCase() + word.substring(1);
    }).join(" ");
    return str;
}

export function itemNameToImage(name: string) {
    name = name.replace(/ /g,'')
    const nameWithoutEmptySpace = name.slice(0, 1).toLowerCase() + name.slice(1)
    return getAvatar(nameWithoutEmptySpace)
}