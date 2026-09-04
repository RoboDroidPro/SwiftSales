package com.example.salestracker

/**
 * This file is reserved for comments and docs about workings and wiring
 * of this app.
 */

/**
 * Possible upgrade is to add an inventory tracking system that will decrease
 * inventory every time you sell something. It would mean adding an Inventory table,
 * having a way to tell the app that you bought something and how much, and
 * then get the app to automatically put OUT OF STOCK on any that is finished.
 */

/**
 * App Hierarchy
 *
 * [MainActivity] calls SaleNavGraph
 * @see com.example.salestracker.ui.navigation.SaleNavGraph
 * SaleNavGraph has the screens. Each of those is a self-contained that
 * has all its stuff inside it.
 * [com.example.salestracker.ui.screens.AllSalesScreen]
 * [com.example.salestracker.ui.screens.sale.add.AddEditSaleScreen]
 * [com.example.salestracker.ui.screens.settings.SettingsScreen]
 *
 */