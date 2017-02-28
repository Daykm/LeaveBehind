package com.daykm.leavebehind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    int oneDip() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());
    }

    int sixteenDip() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = new RecyclerView(this);
        setContentView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        helper.attachToRecyclerView(recyclerView);
    }

    RecyclerView.Adapter<ViewHolder> adapter = new RecyclerView.Adapter<ViewHolder>() {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent.getContext());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView view;

        ViewHolder(Context context) {
            super(new TextView(context));
            view = (TextView) itemView;

            int paddingPix = sixteenDip();

            view.setPadding(paddingPix, paddingPix, paddingPix, paddingPix);

            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TypedValue value = new TypedValue();

            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, value, true);
            view.setClickable(true);
            view.setBackgroundResource(value.resourceId);

        }

        void bind(int position) {
            view.setText(Integer.toString(position));
        }
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            Log.i(MainActivity.class.getSimpleName(), "Direction: " + direction);

            //adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

            viewHolder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }
            }, 2000);
        }

        ColorDrawable drawable = new ColorDrawable(Color.RED);
        Paint bgPaint = new Paint();

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
                float dY,
                int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            bgPaint.setColor(Color.RED);
            if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            if (Math.abs(dX) > Math.abs(dY)) {
                boolean isLeft = dX < 0;

                if (bgPaint.getColor() != Color.TRANSPARENT) {
                    int left = isLeft ? itemView.getRight()+(int) dX : itemView.getLeft();
                    int right = isLeft ? itemView.getRight() : (itemView.getLeft() + (int) dX);
                    c.drawRect(left, itemView.getTop(), right, itemView.getBottom(), bgPaint);
                }
                if (drawable != null) {
                    int itemHeight = itemView.getBottom() - itemView.getTop();
                    int intrinsicWidth = drawable.getIntrinsicWidth();
                    int intrinsicHeight = drawable.getIntrinsicWidth();

                    int left;
                    int right;
                    if (isLeft) {
                        left = itemView.getRight() -  - intrinsicWidth;
                        right = itemView.getRight()  ;
                    } else {
                        left = itemView.getLeft() ;
                        right = itemView.getLeft()  + intrinsicWidth;
                    }
                    int top = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                    int bottom = top + intrinsicHeight;
                    drawable.setBounds(left, top, right, bottom);

                    drawable.draw(c);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
                float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    ItemTouchHelper helper = new ItemTouchHelper(callback);

    private Paint dividerPaint = new Paint();


    RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            dividerPaint.setColor(Color.BLACK);
            dividerPaint.setAlpha(120);
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + oneDip();

                c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, dividerPaint);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                        parent.getResources().getDisplayMetrics());
            }
        }
    };
}
