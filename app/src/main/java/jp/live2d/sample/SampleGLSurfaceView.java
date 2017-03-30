/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.sample;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.util.UtSystem;

public class SampleGLSurfaceView extends GLSurfaceView
{
	private SampleGLRenderer		renderer ;

	public SampleGLSurfaceView(Context context )
	{
		super(context);

		renderer = new SampleGLRenderer() ;
		setRenderer( renderer ) ;
	}


	class SampleGLRenderer implements Renderer
	{
		private Live2DModelAndroid	live2DModel ;
		private final String MODEL_PATH = "haru/haru.moc" ;
		private final String TEXTURE_PATHS[] =
			{
				"haru/haru.1024/texture_00.png" ,
				"haru/haru.1024/texture_01.png" ,
				"haru/haru.1024/texture_02.png"
			} ;

		@Override
		public void onDrawFrame(GL10 gl)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW ) ;
			gl.glLoadIdentity() ;
			gl.glClear( GL10.GL_COLOR_BUFFER_BIT ) ;

			double t = (UtSystem.getUserTimeMSec()/1000.0) * 2 * Math.PI  ;// 1秒ごとに2π(1周期)増える
			double cycle=3.0;// パラメータが一周する時間(秒)
			double sin=Math.sin( t/cycle );// -1から1の間を周期ごとに変化する
			live2DModel.setParamFloat( "PARAM_ANGLE_X" , (float) (30 * sin) ) ;// PARAM_ANGLE_Xのパラメータが[cycle]秒ごとに-30から30まで変化する

			live2DModel.setGL( gl ) ;

			live2DModel.update() ;
			live2DModel.draw() ;
		}


		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			// ビューポートはデバイスの幅と合わせる。画面全体に表示される。
			gl.glViewport( 0 , 0 , width , height ) ;

			// 簡易的にプロジェクション行列一つですべての変換を行う。
			gl.glMatrixMode( GL10.GL_PROJECTION ) ;
			gl.glLoadIdentity() ;

			float modelWidth = live2DModel.getCanvasWidth();// モデラーで設定したキャンバス幅
			float aspect = (float)width/height;

			// 描画範囲の設定 引数はleft,right,bottom,topの順
			gl.glOrthof(0 ,	modelWidth , modelWidth / aspect , 0 , 0.5f , -0.5f ) ;
		}


		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//  モデルの初期化
			try
			{
				InputStream in = getContext().getAssets().open( MODEL_PATH ) ;
				live2DModel = Live2DModelAndroid.loadModel( in ) ;
				in.close() ;

				for (int i = 0 ; i < TEXTURE_PATHS.length ; i++ )
				{
					InputStream tin = getContext().getAssets().open( TEXTURE_PATHS[i] ) ;
					int texNo = UtOpenGL.loadTexture(gl , tin , true ) ;
					live2DModel.setTexture( i , texNo ) ;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
