from sqlalchemy import select, tuple_
from sqlalchemy.orm import Session

class PostgresPaginationPlugin:
    @staticmethod
    def paginate(
        session: Session, 
        model: Any, 
        request: PageRequest
    ) -> PageResult:
        
        sort_col = getattr(model, request.sort_by)
        query = select(model)
        cursor_data = CursorUtil.decode(request.cursor)

        if cursor_data:
            operator = tuple_.__gt__ if request.is_ascending else tuple_.__lt__
            query = query.where(
                operator(
                    tuple_(sort_col, model.id),
                    tuple_(cursor_data.sort_value, cursor_data.id)
                )
            )

        order_col = sort_col.asc() if request.is_ascending else sort_col.desc()
        order_id = model.id.asc() if request.is_ascending else model.id.desc()
        
        query = query.order_by(order_col, order_id).limit(request.limit + 1)
        results = session.execute(query).scalars().all()
        
        has_next = len(results) > request.limit
        data = results[:request.limit]
        
        next_cursor = None
        if data:
            last_item = data[-1]
            last_sort_value = getattr(last_item, request.sort_by)
            next_cursor = CursorUtil.encode(last_sort_value, last_item.id)
            
        return PageResult(
            data=data, 
            next_cursor=next_cursor, 
            has_next=has_next
        )