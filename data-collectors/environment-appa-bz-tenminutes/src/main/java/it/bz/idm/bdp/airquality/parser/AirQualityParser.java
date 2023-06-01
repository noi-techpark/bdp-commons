// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

// Generated from it/bz/idm/bdp/airquality/AirQuality.g4 by ANTLR 4.7.1
package it.bz.idm.bdp.airquality.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AirQualityParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, SIGN=6, EOL=7, DIGIT=8, UPPERCASE=9;
	public static final int
		RULE_dataset = 0, RULE_row = 1, RULE_blockno = 2, RULE_station = 3, RULE_time = 4, 
		RULE_date = 5, RULE_terminator = 6, RULE_number = 7, RULE_block = 8, RULE_keyval = 9, 
		RULE_decimal = 10, RULE_real = 11;
	public static final String[] ruleNames = {
		"dataset", "row", "blockno", "station", "time", "date", "terminator", 
		"number", "block", "keyval", "decimal", "real"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "'ST'", "'.'", "'#'", "'*'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "SIGN", "EOL", "DIGIT", "UPPERCASE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "AirQuality.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AirQualityParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DatasetContext extends ParserRuleContext {
		public List<RowContext> row() {
			return getRuleContexts(RowContext.class);
		}
		public RowContext row(int i) {
			return getRuleContext(RowContext.class,i);
		}
		public DatasetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterDataset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitDataset(this);
		}
	}

	public final DatasetContext dataset() throws RecognitionException {
		DatasetContext _localctx = new DatasetContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_dataset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(24);
				row();
				}
				}
				setState(27); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RowContext extends ParserRuleContext {
		public StationContext station() {
			return getRuleContext(StationContext.class,0);
		}
		public List<DecimalContext> decimal() {
			return getRuleContexts(DecimalContext.class);
		}
		public DecimalContext decimal(int i) {
			return getRuleContext(DecimalContext.class,i);
		}
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public DateContext date() {
			return getRuleContext(DateContext.class,0);
		}
		public BlocknoContext blockno() {
			return getRuleContext(BlocknoContext.class,0);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminatorContext terminator() {
			return getRuleContext(TerminatorContext.class,0);
		}
		public RowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterRow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitRow(this);
		}
	}

	public final RowContext row() throws RecognitionException {
		RowContext _localctx = new RowContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_row);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			station();
			setState(30);
			match(T__0);
			setState(31);
			decimal();
			setState(32);
			match(T__0);
			setState(33);
			time();
			setState(34);
			match(T__0);
			setState(35);
			date();
			setState(36);
			match(T__0);
			setState(37);
			decimal();
			setState(38);
			match(T__0);
			setState(39);
			blockno();
			setState(40);
			match(T__0);
			setState(41);
			block();
			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(42);
					match(T__0);
					setState(43);
					block();
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(49);
			match(T__0);
			setState(50);
			terminator();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlocknoContext extends ParserRuleContext {
		public TerminalNode UPPERCASE() { return getToken(AirQualityParser.UPPERCASE, 0); }
		public DecimalContext decimal() {
			return getRuleContext(DecimalContext.class,0);
		}
		public BlocknoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockno; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterBlockno(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitBlockno(this);
		}
	}

	public final BlocknoContext blockno() throws RecognitionException {
		BlocknoContext _localctx = new BlocknoContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_blockno);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(UPPERCASE);
			setState(53);
			decimal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StationContext extends ParserRuleContext {
		public DecimalContext decimal() {
			return getRuleContext(DecimalContext.class,0);
		}
		public StationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_station; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterStation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitStation(this);
		}
	}

	public final StationContext station() throws RecognitionException {
		StationContext _localctx = new StationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_station);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(T__1);
			setState(56);
			decimal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TimeContext extends ParserRuleContext {
		public List<DecimalContext> decimal() {
			return getRuleContexts(DecimalContext.class);
		}
		public DecimalContext decimal(int i) {
			return getRuleContext(DecimalContext.class,i);
		}
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitTime(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			decimal();
			setState(59);
			match(T__2);
			setState(60);
			decimal();
			setState(61);
			match(T__2);
			setState(62);
			decimal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DateContext extends ParserRuleContext {
		public List<DecimalContext> decimal() {
			return getRuleContexts(DecimalContext.class);
		}
		public DecimalContext decimal(int i) {
			return getRuleContext(DecimalContext.class,i);
		}
		public DateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_date; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterDate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitDate(this);
		}
	}

	public final DateContext date() throws RecognitionException {
		DateContext _localctx = new DateContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_date);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			decimal();
			setState(65);
			match(T__0);
			setState(66);
			decimal();
			setState(67);
			match(T__0);
			setState(68);
			decimal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TerminatorContext extends ParserRuleContext {
		public DecimalContext decimal() {
			return getRuleContext(DecimalContext.class,0);
		}
		public TerminalNode EOL() { return getToken(AirQualityParser.EOL, 0); }
		public TerminatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterTerminator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitTerminator(this);
		}
	}

	public final TerminatorContext terminator() throws RecognitionException {
		TerminatorContext _localctx = new TerminatorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_terminator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__3);
			setState(71);
			decimal();
			setState(72);
			match(T__0);
			setState(73);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public DecimalContext decimal() {
			return getRuleContext(DecimalContext.class,0);
		}
		public RealContext real() {
			return getRuleContext(RealContext.class,0);
		}
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_number);
		try {
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(75);
				decimal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				real();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(77);
				match(T__4);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public DecimalContext decimal() {
			return getRuleContext(DecimalContext.class,0);
		}
		public List<KeyvalContext> keyval() {
			return getRuleContexts(KeyvalContext.class);
		}
		public KeyvalContext keyval(int i) {
			return getRuleContext(KeyvalContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_block);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			decimal();
			setState(81);
			match(T__0);
			setState(82);
			keyval();
			setState(87);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(83);
					match(T__0);
					setState(84);
					keyval();
					}
					} 
				}
				setState(89);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyvalContext extends ParserRuleContext {
		public TerminalNode UPPERCASE() { return getToken(AirQualityParser.UPPERCASE, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public KeyvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterKeyval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitKeyval(this);
		}
	}

	public final KeyvalContext keyval() throws RecognitionException {
		KeyvalContext _localctx = new KeyvalContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_keyval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(UPPERCASE);
			setState(91);
			match(T__0);
			setState(92);
			number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DecimalContext extends ParserRuleContext {
		public TerminalNode SIGN() { return getToken(AirQualityParser.SIGN, 0); }
		public List<TerminalNode> DIGIT() { return getTokens(AirQualityParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(AirQualityParser.DIGIT, i);
		}
		public DecimalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterDecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitDecimal(this);
		}
	}

	public final DecimalContext decimal() throws RecognitionException {
		DecimalContext _localctx = new DecimalContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_decimal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SIGN) {
				{
				setState(94);
				match(SIGN);
				}
			}

			setState(98); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(97);
				match(DIGIT);
				}
				}
				setState(100); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DIGIT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RealContext extends ParserRuleContext {
		public TerminalNode SIGN() { return getToken(AirQualityParser.SIGN, 0); }
		public List<TerminalNode> DIGIT() { return getTokens(AirQualityParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(AirQualityParser.DIGIT, i);
		}
		public RealContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_real; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).enterReal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AirQualityListener ) ((AirQualityListener)listener).exitReal(this);
		}
	}

	public final RealContext real() throws RecognitionException {
		RealContext _localctx = new RealContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_real);
		int _la;
		try {
			int _alt;
			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SIGN) {
					{
					setState(102);
					match(SIGN);
					}
				}

				setState(105);
				match(T__2);
				setState(107); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(106);
					match(DIGIT);
					}
					}
					setState(109); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==DIGIT );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SIGN) {
					{
					setState(111);
					match(SIGN);
					}
				}

				setState(115); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(114);
					match(DIGIT);
					}
					}
					setState(117); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==DIGIT );
				setState(119);
				match(T__2);
				setState(121); 
				_errHandler.sync(this);
				_alt = 1+1;
				do {
					switch (_alt) {
					case 1+1:
						{
						{
						setState(120);
						match(DIGIT);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(123); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13\u0082\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\3\2\6\2\34\n\2\r\2\16\2\35\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3/\n\3\f\3\16\3\62\13\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\5\tQ\n\t\3\n\3\n\3\n\3\n\3\n"+
		"\7\nX\n\n\f\n\16\n[\13\n\3\13\3\13\3\13\3\13\3\f\5\fb\n\f\3\f\6\fe\n\f"+
		"\r\f\16\ff\3\r\5\rj\n\r\3\r\3\r\6\rn\n\r\r\r\16\ro\3\r\5\rs\n\r\3\r\6"+
		"\rv\n\r\r\r\16\rw\3\r\3\r\6\r|\n\r\r\r\16\r}\5\r\u0080\n\r\3\r\3}\2\16"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\2\2\2\u0082\2\33\3\2\2\2\4\37\3\2\2\2\6"+
		"\66\3\2\2\2\b9\3\2\2\2\n<\3\2\2\2\fB\3\2\2\2\16H\3\2\2\2\20P\3\2\2\2\22"+
		"R\3\2\2\2\24\\\3\2\2\2\26a\3\2\2\2\30\177\3\2\2\2\32\34\5\4\3\2\33\32"+
		"\3\2\2\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\3\3\2\2\2\37 \5"+
		"\b\5\2 !\7\3\2\2!\"\5\26\f\2\"#\7\3\2\2#$\5\n\6\2$%\7\3\2\2%&\5\f\7\2"+
		"&\'\7\3\2\2\'(\5\26\f\2()\7\3\2\2)*\5\6\4\2*+\7\3\2\2+\60\5\22\n\2,-\7"+
		"\3\2\2-/\5\22\n\2.,\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\63"+
		"\3\2\2\2\62\60\3\2\2\2\63\64\7\3\2\2\64\65\5\16\b\2\65\5\3\2\2\2\66\67"+
		"\7\13\2\2\678\5\26\f\28\7\3\2\2\29:\7\4\2\2:;\5\26\f\2;\t\3\2\2\2<=\5"+
		"\26\f\2=>\7\5\2\2>?\5\26\f\2?@\7\5\2\2@A\5\26\f\2A\13\3\2\2\2BC\5\26\f"+
		"\2CD\7\3\2\2DE\5\26\f\2EF\7\3\2\2FG\5\26\f\2G\r\3\2\2\2HI\7\6\2\2IJ\5"+
		"\26\f\2JK\7\3\2\2KL\7\t\2\2L\17\3\2\2\2MQ\5\26\f\2NQ\5\30\r\2OQ\7\7\2"+
		"\2PM\3\2\2\2PN\3\2\2\2PO\3\2\2\2Q\21\3\2\2\2RS\5\26\f\2ST\7\3\2\2TY\5"+
		"\24\13\2UV\7\3\2\2VX\5\24\13\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2"+
		"Z\23\3\2\2\2[Y\3\2\2\2\\]\7\13\2\2]^\7\3\2\2^_\5\20\t\2_\25\3\2\2\2`b"+
		"\7\b\2\2a`\3\2\2\2ab\3\2\2\2bd\3\2\2\2ce\7\n\2\2dc\3\2\2\2ef\3\2\2\2f"+
		"d\3\2\2\2fg\3\2\2\2g\27\3\2\2\2hj\7\b\2\2ih\3\2\2\2ij\3\2\2\2jk\3\2\2"+
		"\2km\7\5\2\2ln\7\n\2\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2p\u0080"+
		"\3\2\2\2qs\7\b\2\2rq\3\2\2\2rs\3\2\2\2su\3\2\2\2tv\7\n\2\2ut\3\2\2\2v"+
		"w\3\2\2\2wu\3\2\2\2wx\3\2\2\2xy\3\2\2\2y{\7\5\2\2z|\7\n\2\2{z\3\2\2\2"+
		"|}\3\2\2\2}~\3\2\2\2}{\3\2\2\2~\u0080\3\2\2\2\177i\3\2\2\2\177r\3\2\2"+
		"\2\u0080\31\3\2\2\2\16\35\60PYafiorw}\177";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}