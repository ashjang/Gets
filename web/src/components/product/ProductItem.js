import React, {useCallback, useEffect, useState} from "react";
import ProductCard from "./ProductCard";
import axios from "axios";
import {useTranslation} from "react-i18next";

const ProductItem = (props) => {
    const {t, i18n} = useTranslation()
    useEffect(() => {
        axios.get('http://localhost:3000//list/:type')
    })
    return (
        <div style={{ marginTop: 70}}>
            <div className="my_location">
                <h4>{t("category")}</h4>
                <h4 className="location_arrow">{'>'}</h4>
                <h4>{props.location}</h4>
               <h4 className="location_arrow">{'>'}</h4>
                <h4>{props.detailLocation}</h4>
            </div>
        <ProductCard />
        </div>
    )
}

export default ProductItem