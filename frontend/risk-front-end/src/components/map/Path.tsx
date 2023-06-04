import {LegacyRef} from "react";

interface PathRouteProps {
    d: string;
    name: string;
    innerRef: LegacyRef<SVGPathElement> | undefined;
}

/**
 * specific shape of the Territory
 */
export default function Path({d, name, innerRef}: PathRouteProps) {
    return <path
        ref={innerRef}
        className="territory"
        id={name}
        d={d}/>
}
