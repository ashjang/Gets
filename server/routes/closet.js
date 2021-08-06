const express = require('express')
const router = express.Router()
const connection = require('../lib/mysql')

module.exports = function (passport) {
	router.get('/', function (req, res, next) {
		res.send("closet")
	});
	
	router.get('/product', (req, res) => {
		if (req.user) {
			connection.query('select * from favoriteProduct where `userEmail`=?', [req.user.email],
				(err, result) => {
					if (err || result.length === 0)
						res.send({result: false, error: "notMatchProduct"})
					else {
						res.send(result.map(it => it["productID"]))
					}
				})
		} else res.send({"result": false})
	})
	
	router.get('/coordination', (req, res) => {
		if (req.user) {
			connection.query(`select *
                              from favoriteCoordination
                              where userEmail = ?`, [req.user.email],
				(err, result) => {
					if (err || result.length === 0)
						res.send({result: false, error: "notMatchCoordination"})
					else {
						res.send(result.map(it => it["coordinationID"]))
					}
				})
		} else res.send({"result": false})
	})
	
	return router
}