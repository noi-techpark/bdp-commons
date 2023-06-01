// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

// Generated from it/bz/idm/bdp/airquality/AirQuality.g4 by ANTLR 4.7.1
package it.bz.idm.bdp.airquality.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AirQualityParser}.
 */
public interface AirQualityListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#dataset}.
	 * @param ctx the parse tree
	 */
	void enterDataset(AirQualityParser.DatasetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#dataset}.
	 * @param ctx the parse tree
	 */
	void exitDataset(AirQualityParser.DatasetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#row}.
	 * @param ctx the parse tree
	 */
	void enterRow(AirQualityParser.RowContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#row}.
	 * @param ctx the parse tree
	 */
	void exitRow(AirQualityParser.RowContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#blockno}.
	 * @param ctx the parse tree
	 */
	void enterBlockno(AirQualityParser.BlocknoContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#blockno}.
	 * @param ctx the parse tree
	 */
	void exitBlockno(AirQualityParser.BlocknoContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#station}.
	 * @param ctx the parse tree
	 */
	void enterStation(AirQualityParser.StationContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#station}.
	 * @param ctx the parse tree
	 */
	void exitStation(AirQualityParser.StationContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(AirQualityParser.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(AirQualityParser.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDate(AirQualityParser.DateContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDate(AirQualityParser.DateContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#terminator}.
	 * @param ctx the parse tree
	 */
	void enterTerminator(AirQualityParser.TerminatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#terminator}.
	 * @param ctx the parse tree
	 */
	void exitTerminator(AirQualityParser.TerminatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(AirQualityParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(AirQualityParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(AirQualityParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(AirQualityParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#keyval}.
	 * @param ctx the parse tree
	 */
	void enterKeyval(AirQualityParser.KeyvalContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#keyval}.
	 * @param ctx the parse tree
	 */
	void exitKeyval(AirQualityParser.KeyvalContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#decimal}.
	 * @param ctx the parse tree
	 */
	void enterDecimal(AirQualityParser.DecimalContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#decimal}.
	 * @param ctx the parse tree
	 */
	void exitDecimal(AirQualityParser.DecimalContext ctx);
	/**
	 * Enter a parse tree produced by {@link AirQualityParser#real}.
	 * @param ctx the parse tree
	 */
	void enterReal(AirQualityParser.RealContext ctx);
	/**
	 * Exit a parse tree produced by {@link AirQualityParser#real}.
	 * @param ctx the parse tree
	 */
	void exitReal(AirQualityParser.RealContext ctx);
}