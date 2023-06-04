import React, {useEffect, useRef, useState} from 'react';
import Path from './Path';
import "./Territory.css";

interface TerritoryProps {
    drawPath: string;
    name: string;
    troopCount?: number;
    callback: (e: any, country: string) => void;
    xOffset: number;
    yOffset: number;
}

/**
 * A Territory on the map
 */
export default function Territory({drawPath, name, troopCount=0, callback, xOffset, yOffset}: TerritoryProps) {
    const ref = useRef<SVGPathElement>(null);
    const [centerX, setCenterX] = useState(0);
    const [centerY, setCenterY] = useState(0);

    useEffect(() => {
        if (ref.current) {
            let bo = ref.current.getBBox();
            setCenterX(bo.x + (bo.width / 2));
            setCenterY(bo.y + (bo.height / 2));
        }
    }, [])

    // offset to center the troopcount in the circle
    // optional: fix for triple digits
    const digitXOffset = troopCount > 9 ? -5 : 0;

    return <g className="territory" onClick={(e) => callback(e, name)}>
        <Path name={name} d={drawPath} innerRef={ref}/>
        <circle cx={centerX + xOffset + 4} cy={centerY + yOffset - 6} r={11}/>
        <text x={centerX + xOffset + digitXOffset} y={centerY + yOffset}>{troopCount}</text>
    </g>
}
